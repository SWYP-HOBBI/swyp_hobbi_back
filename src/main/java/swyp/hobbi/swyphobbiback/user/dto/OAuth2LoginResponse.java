package swyp.hobbi.swyphobbiback.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuth2LoginResponse {
    private String message;
    private String accessToken;
}