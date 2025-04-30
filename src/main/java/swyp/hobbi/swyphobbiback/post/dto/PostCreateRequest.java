package swyp.hobbi.swyphobbiback.post.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PostCreateRequest {
    private String title;
    private String content;
    private List<String> hobbyTagNames;
}
