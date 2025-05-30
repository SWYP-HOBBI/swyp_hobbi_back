package swyp.hobbi.swyphobbiback.like.dto;

import lombok.Getter;
import swyp.hobbi.swyphobbiback.like.domain.Like;

import java.time.LocalDateTime;

@Getter
public class LikeResponse {
    private Long postLikeId;
    private Long postId;
    private Long userId;
    private Boolean likeYn;
    private LocalDateTime createdAt;

    public static LikeResponse from(Like like) {
        LikeResponse response = new LikeResponse();
        response.postLikeId = like.getPostLikeId();
        response.postId = like.getPost().getPostId();
        response.userId = like.getUser().getUserId();
        response.likeYn = like.getLikeYn();
        response.createdAt = like.getCreatedAt();

        return response;
    }
}
