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
public class MyModifyPageResponse {

    private String username;
    private String email;
    private String gender;
    private String nickname;
    private String mbti;
    private String userImageUrl;
    private Integer birthYear;
    private Integer birthMonth;
    private Integer birthDay;
    private List<String> hobbyTags;

}
