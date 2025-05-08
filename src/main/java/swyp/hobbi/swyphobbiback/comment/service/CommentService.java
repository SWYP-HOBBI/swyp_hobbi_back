package swyp.hobbi.swyphobbiback.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp.hobbi.swyphobbiback.comment.domain.Comment;
import swyp.hobbi.swyphobbiback.comment.dto.CommentRequest;
import swyp.hobbi.swyphobbiback.comment.dto.CommentResponse;
import swyp.hobbi.swyphobbiback.comment.repository.CommentRepository;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CommentNotFoundException;
import swyp.hobbi.swyphobbiback.common.exception.PostNotFoundException;
import swyp.hobbi.swyphobbiback.common.exception.UserNotFoundException;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post.repository.PostRepository;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;

import java.util.List;

import static java.util.function.Predicate.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponse create(CustomUserDetails userDetails, CommentRequest request) {
        if(userDetails == null || userDetails.getUsername() == null) {
            throw new AccessDeniedException(ErrorCode.FORBIDDEN.getMessage());
        }
        Comment parentComment = findParentComment(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(PostNotFoundException::new);

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .parentCommentId(parentComment == null ? null : parentComment.getCommentId())
                .commentContent(request.getContent())
                .deleted(false)
                .build();

        return CommentResponse.from(commentRepository.save(comment));
    }

    private Comment findParentComment(CommentRequest request) {
        Long parentCommentId = request.getParentCommentId();

        if(parentCommentId == null) {
            return null;
        }

        return commentRepository.findById(parentCommentId)
                .filter(not(Comment::getDeleted))
                .filter(Comment::isRoot)
                .orElseThrow(CommentNotFoundException::new);
    }

    @Transactional
    public CommentResponse update(CustomUserDetails userDetails, Long commentId, CommentRequest request) {
        if(!userDetails.getUserId().equals(request.getUserId())) {
            throw new AccessDeniedException(ErrorCode.FORBIDDEN.getMessage());
        }

        Comment comment = commentRepository.findById(commentId)
                .filter(not(Comment::getDeleted))
                .orElseThrow(CommentNotFoundException::new);

        comment.update(request.getContent());

        return CommentResponse.from(comment);
    }

    public List<CommentResponse> getCommentsInfiniteScroll(Long postId, Long lastCommentId, Integer pageSize) {
        List<Long> commentIds = fetchCommentIds(postId, pageSize, lastCommentId);
        List<Comment> comments = commentRepository.findCommentWithUser(commentIds);

        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }

    private List<Long> fetchCommentIds(Long postId, Integer pageSize, Long lastCommentId) {
        boolean isFirstPage = lastCommentId == null || lastCommentId == 0;
        if(isFirstPage) {
            return commentRepository.findCommentIds(postId, pageSize);
        }
        return commentRepository.findCommentIds(postId, lastCommentId, pageSize);
    }

    // 논리적으로 삭제
    @Transactional
    public void deleteLogicOnly(CustomUserDetails userDetails, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if(!userDetails.getUserId().equals(comment.getUser().getUserId())) {
            throw new AccessDeniedException(ErrorCode.FORBIDDEN.getMessage());
        }

        commentRepository.findById(commentId)
                .filter(not(Comment::getDeleted))
                .ifPresent(c -> {
                    if(hasChildren(c)) {
                        c.setDeletedTrue();
                    } else {
                        delete(c);
                    }
                });
    }

    private boolean hasChildren(Comment comment) {
        return commentRepository.countBy(comment.getPost().getPostId(), comment.getCommentId(), 1L) == 1;
    }

    // 실제 DB 삭제
    private void delete(Comment comment) {
        commentRepository.delete(comment);

        if(!comment.isRoot()) {
            commentRepository.findById(comment.getParentCommentId())
                    .filter(Comment::getDeleted)
                    .filter(not(this::hasChildren))
                    .ifPresent(this::delete);
        }
    }
}
