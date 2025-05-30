package swyp.hobbi.swyphobbiback.comment.dto;

import lombok.Getter;
import lombok.ToString;
import swyp.hobbi.swyphobbiback.comment.domain.Comment;

import java.time.LocalDateTime;

@Getter
@ToString
public class CommentResponse {
    private Long commentId;
    private String content;
    private Long userId;
    private String nickname;
    private String userImageUrl;
    private Long parentCommentId;
    private Long postId;
    private Boolean deleted;
    private Integer userLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse from(Comment comment, Integer userLevel) {
        CommentResponse response = new CommentResponse();
        response.commentId = comment.getCommentId();
        response.content = comment.getCommentContent();
        response.userId = comment.getUser().getUserId();
        response.nickname = comment.getUser().getNickname();
        response.userImageUrl = comment.getUser().getUserImageUrl();
        response.parentCommentId = comment.getParentCommentId();
        response.postId = comment.getPost().getPostId();
        response.deleted = comment.getDeleted();
        response.userLevel = userLevel;
        response.createdAt = comment.getCreatedAt();
        response.updatedAt = comment.getUpdatedAt();

        return response;
    }
}
