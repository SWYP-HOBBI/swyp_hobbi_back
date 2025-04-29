package swyp.hobbi.swyphobbiback.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    private String email;
    private String username;
    private String password;
    private String passwordConfirm;
    private Integer birthYear;
    private Integer birthMonth;
    private Integer birthDay;
    private String gender; //'F', 'M'
    private String nickname;
    private String mbti;
    private String userImageUrl;
    private List<String> hobbyTags;
}
