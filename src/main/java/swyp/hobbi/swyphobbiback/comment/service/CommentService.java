package swyp.hobbi.swyphobbiback.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp.hobbi.swyphobbiback.comment.domain.Comment;
import swyp.hobbi.swyphobbiback.comment.dto.CommentCreateRequest;
import swyp.hobbi.swyphobbiback.comment.dto.CommentResponse;
import swyp.hobbi.swyphobbiback.comment.dto.CommentUpdateRequest;
import swyp.hobbi.swyphobbiback.comment.repository.CommentRepository;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CommentNotFoundException;
import swyp.hobbi.swyphobbiback.common.exception.PostNotFoundException;
import swyp.hobbi.swyphobbiback.common.exception.UserNotFoundException;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.notification.domain.NotificationType;
import swyp.hobbi.swyphobbiback.notification.service.NotificationService;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post.repository.PostRepository;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;
import swyp.hobbi.swyphobbiback.user_rank.dto.UserLevelProjection;
import swyp.hobbi.swyphobbiback.user_rank.repository.UserRankRepository;
import swyp.hobbi.swyphobbiback.user_rank.service.UserRankService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Predicate.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserRankRepository userRankRepository;
    private final NotificationService notificationService;
    private final UserRankService userRankService;

    @Transactional
    public CommentResponse create(CustomUserDetails userDetails, CommentCreateRequest request) {
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

        Integer userLevel = userRankService.getUserLevel(userDetails.getUser());

        Long receiverId = post.getUser().getUserId();
        Long senderId = user.getUserId();

        if(!receiverId.equals(senderId)) {
            notificationService.sendNotification(
                    receiverId, senderId, comment.getCommentContent(), NotificationType.COMMENT, post.getPostId()
            );
        }

        return CommentResponse.from(commentRepository.save(comment), userLevel);
    }

    private Comment findParentComment(CommentCreateRequest request) {
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
    public CommentResponse update(CustomUserDetails userDetails, Long commentId, CommentUpdateRequest request) {
        if(!userDetails.getUserId().equals(request.getUserId())) {
            throw new AccessDeniedException(ErrorCode.FORBIDDEN.getMessage());
        }

        Comment comment = commentRepository.findById(commentId)
                .filter(not(Comment::getDeleted))
                .orElseThrow(CommentNotFoundException::new);

        comment.update(request.getContent());
        Integer userLevel = userRankService.getUserLevel(userDetails.getUser());


        return CommentResponse.from(comment, userLevel);
    }

    public List<CommentResponse> getCommentsInfiniteScroll(Long postId, Long lastCommentId, Integer pageSize) {
        List<Long> commentIds = fetchCommentIds(postId, pageSize, lastCommentId);
        List<Comment> comments = commentRepository.findCommentWithUser(commentIds);
        List<Long> userIds = userRepository.findUserIdsByCommentIds(commentIds);

        Map<Long, Integer> userLevelMap = userRankRepository.findUserLevelByUserIds(userIds).stream()
                .collect(Collectors.toMap(UserLevelProjection::getUserId, UserLevelProjection::getUserLevel));

        return comments.stream()
                .map(comment -> {
                    Integer userLevel = userLevelMap.getOrDefault(comment.getUser().getUserId(), 1);
                    return CommentResponse.from(comment, userLevel);
                })
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
