package swyp.hobbi.swyphobbiback.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OauthLoginStatusResponse {
    private Boolean kakao;
    private Boolean google;
}
