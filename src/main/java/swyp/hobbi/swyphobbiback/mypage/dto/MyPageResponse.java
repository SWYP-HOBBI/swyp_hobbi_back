package swyp.hobbi.swyphobbiback.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponse {

    private Long userId;
    private String username;
    private String nickname;
    private String mbti;
    private List<String> hobbyTags;
    private String userImageUrl;

}
