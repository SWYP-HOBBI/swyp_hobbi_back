package swyp.hobbi.swyphobbiback.comment.dto;

import lombok.Getter;

@Getter
public class CommentUpdateRequest {
    private Long postId;
    private String content;
    private Long userId;
}
