package swyp.hobbi.swyphobbiback.post.dto;

import lombok.Getter;
import lombok.ToString;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post_image.domain.PostImage;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class PostResponse {
    private Long postId;
    private String nickname;
    private String userImageUrl;
    private Long userId;
    private String title;
    private String content;
    private List<String> postImageUrls;
    private List<String> postHobbyTags;
    private Long commentCount;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post, Long commentCount, Long likeCount) {
        PostResponse response = new PostResponse();
        response.postId = post.getPostId();
        response.nickname = post.getUser().getNickname();
        response.userImageUrl = post.getUser().getUserImageUrl();
        response.userId = post.getUser().getUserId();
        response.title = post.getPostTitle();
        response.content = post.getPostContent();
        response.postImageUrls = post.getPostImages().stream()
                .map(PostImage::getImageUrl)
                .toList();
        response.postHobbyTags = post.getPostHobbyTags().stream()
                .map(postHobbyTag -> postHobbyTag.getHobbyTag().getHobbyTagName())
                .toList();
        response.commentCount = commentCount == null ? 0L : commentCount;
        response.likeCount = likeCount == null ? 0L : likeCount;
        response.createdAt = post.getCreatedAt();
        response.updatedAt = post.getUpdatedAt();

        return response;
    }
}
