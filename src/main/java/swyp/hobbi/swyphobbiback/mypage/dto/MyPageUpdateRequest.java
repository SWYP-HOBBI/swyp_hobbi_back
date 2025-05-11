package swyp.hobbi.swyphobbiback.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageUpdateRequest {
    private String username;
    private String gender;
    private String mbti;
    private Integer birthYear;
    private Integer birthMonth;
    private Integer birthDay;
    private List<String> hobbyTags;
}
