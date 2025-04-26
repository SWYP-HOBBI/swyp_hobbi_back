package swyp.hobbi.swyphobbiback.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReissueResponse {
    private String accessToken;
}
