package swyp.hobbi.swyphobbiback.post.dto;

import lombok.Getter;
import lombok.ToString;
import swyp.hobbi.swyphobbiback.post.domain.Post;

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
                .map()
                .toList();
        response.postHobbyTags = post.getPostHobbyTags().stream()
                .map()
                .toList();
        response.createdAt = post.getCreatedAt();
        response.updatedAt = post.getUpdatedAt();
    }
}
