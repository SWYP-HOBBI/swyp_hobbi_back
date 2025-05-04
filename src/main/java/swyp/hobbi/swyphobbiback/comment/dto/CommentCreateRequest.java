package swyp.hobbi.swyphobbiback.comment.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommentCreateRequest {
    private Long postId;
    private String comment;
    private Long parentCommentId;
    private Long userId;
}
