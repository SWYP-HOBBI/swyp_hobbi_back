package swyp.hobbi.swyphobbiback.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.security.JwtTokenProvider;
import swyp.hobbi.swyphobbiback.token.domain.RefreshToken;
import swyp.hobbi.swyphobbiback.token.dto.ReissueRequest;
import swyp.hobbi.swyphobbiback.token.dto.ReissueResponse;
import swyp.hobbi.swyphobbiback.token.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public ReissueResponse reissue(ReissueRequest request) {
        String refreshToken = request.getRefreshToken();

        String email = jwtTokenProvider.getEmailFromToken(refreshToken); //토큰에서 이메일 추출

        RefreshToken dbToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPIRED_TOKEN));

        if (!dbToken.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN); //db 토큰과 비교
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(email); //새로운 Access 토큰

        return ReissueResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
