package swyp.hobbi.swyphobbiback.post.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PostCreateRequest {
    private String postTitle;
    private String postContent;
    private Long userId;
    private List<String> hobbyTagNames;
}
