package swyp.hobbi.swyphobbiback.post.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PostUpdateRequest {
    private String title;
    private String content;
    private List<String> deletedImageUrls;
    private List<String> hobbyTagNames;
}
