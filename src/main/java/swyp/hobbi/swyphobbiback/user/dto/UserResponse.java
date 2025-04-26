package swyp.hobbi.swyphobbiback.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse { //자동로그인 구현 필요
    private Long userId;
    private String accessToken;
    private String refreshToken;
}
