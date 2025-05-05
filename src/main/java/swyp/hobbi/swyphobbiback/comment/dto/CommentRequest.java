package swyp.hobbi.swyphobbiback.comment.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommentRequest {
    private Long postId;
    private String content;
    private Long parentCommentId;
    private Long userId;
}
