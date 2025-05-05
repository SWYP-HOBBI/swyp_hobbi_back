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
}
