package swyp.hobbi.swyphobbiback.post.dto;

import lombok.Getter;
import lombok.ToString;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post_hobbytag.domain.PostHobbyTag;
import swyp.hobbi.swyphobbiback.post_image.domain.PostImage;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class PostResponse {
    private Long postId;
    private Long userId;
    private String title;
    private String content;
    private List<String> postImageUrls;
    private List<String> postHobbyTags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post) {
        PostResponse response = new PostResponse();
        response.postId = post.getPostId();
        response.userId = post.getUser().getUserId();
        response.title = post.getPostTitle();
        response.content = post.getPostContent();
        response.postImageUrls = post.getPostImages().stream()
                .map(PostImage::getImageUrl)
                .toList();
        response.postHobbyTags = post.getPostHobbyTags().stream()
                .map(postHobbyTag -> postHobbyTag.getHobbyTag().getHobbyTagName())
                .toList();
        response.createdAt = post.getCreatedAt();
        response.updatedAt = post.getUpdatedAt();

        return response;
    }
}
