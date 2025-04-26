package swyp.hobbi.swyphobbiback.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.common.security.JwtTokenProvider;
import swyp.hobbi.swyphobbiback.token.domain.RefreshToken;
import swyp.hobbi.swyphobbiback.token.dto.TokenPair;
import swyp.hobbi.swyphobbiback.token.repository.RefreshTokenRepository;


@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenPair generateAndSaveTokens(String email) { //액세스 토큰과 리프레쉬 토큰 발급하고 저장
        String accessToken = jwtTokenProvider.createAccessToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        RefreshToken tokenSave = RefreshToken.builder()
                .refreshToken(refreshToken)
                .email(email)
                .build();

        refreshTokenRepository.save(tokenSave);
        return new TokenPair(accessToken, refreshToken);
    }
}
