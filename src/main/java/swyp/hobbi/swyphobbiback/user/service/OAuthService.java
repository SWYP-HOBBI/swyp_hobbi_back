package swyp.hobbi.swyphobbiback.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.token.dto.TokenPair;
import swyp.hobbi.swyphobbiback.token.service.TokenService;
import swyp.hobbi.swyphobbiback.user.domain.OauthLogin;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.dto.LoginResponse;
import swyp.hobbi.swyphobbiback.user.dto.OauthLoginStatusResponse;
import swyp.hobbi.swyphobbiback.user.oauth.GoogleOAuthClient;
import swyp.hobbi.swyphobbiback.user.oauth.KakaoOAuthClient;
import swyp.hobbi.swyphobbiback.user.oauth.OAuth2UserInfo;
import swyp.hobbi.swyphobbiback.user.repository.OAuthLoginRepository;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

    private final OAuthLoginRepository oauthLoginRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final GoogleOAuthClient googleOAuthClient;
    private final RedisTemplate<String, String> redisTemplate;

    public LoginResponse handleOAuthLogin(String provider, String code) {
        OAuth2UserInfo userInfo;
        String accessToken;

        switch (provider.toLowerCase()) {
            case "kakao" -> {
                accessToken = kakaoOAuthClient.getAccessToken(code);
                userInfo = kakaoOAuthClient.getUserInfo(accessToken);
            }
            case "google" -> {
                accessToken = googleOAuthClient.getAccessToken(code);
                userInfo = googleOAuthClient.getUserInfo(accessToken);
            }
            default -> throw new CustomException(ErrorCode.INVALID_OAUTH_PROVIDER); //지원하지 않는 소셜 로그인
        }

        Optional<OauthLogin> oauthLogin = oauthLoginRepository.findByProviderEmailAndProvider(userInfo.getEmail(), provider);

        if (oauthLogin.isEmpty()) {
            try {
                redisTemplate.opsForValue().set("email", userInfo.getEmail(), Duration.ofMinutes(10));
                redisTemplate.opsForValue().set("provider", provider, Duration.ofMinutes(10));

                return LoginResponse.builder()
                        .accessToken(null)
                        .refreshToken(null)
                        .userId(null)
                        .build();
            } catch (Exception e) {
                throw new CustomException(ErrorCode.REDIS_WRITE_FAILED);
            }
        }

        User user = userRepository.findByUserIdAndIsDeletedFalse(oauthLogin.get().getUser().getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        TokenPair tokens = tokenService.generateAndSaveTokens(user.getEmail()); // 토큰 생성

        return LoginResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .userId(user.getUserId())
                .build();
    }

    public void linkSocialAccount(Long userId) {
        //임시 저장 정보 Redis로 불러오기
        String email = redisTemplate.opsForValue().get("email");
        String provider = redisTemplate.opsForValue().get("provider");

        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (email == null || provider == null) {
            throw new CustomException(ErrorCode.SOCIAL_INFO_NOT_FOUND);
        }

        if (oauthLoginRepository.existsByProviderEmailAndProvider(email, provider)) {
            throw new CustomException(ErrorCode.SOCIAL_ALREADY_LINKED); // 이미 연동된 계정
        }

        OauthLogin link = OauthLogin.builder() //소셜 계정 연동
                .user(user)
                .provider(provider)
                .providerEmail(email)
                .build();

        oauthLoginRepository.save(link);

        // 연동 완료 후 Redis 정보 삭제
        redisTemplate.delete("email");
        redisTemplate.delete("provider");

    }

    public OauthLoginStatusResponse getLinkStatus(Long userId) { // 마이페이지 연동 확인

        Boolean kakao = oauthLoginRepository.existsByUserUserIdAndProvider(userId, "kakao");
        Boolean google = oauthLoginRepository.existsByUserUserIdAndProvider(userId, "google");

        return OauthLoginStatusResponse.builder()
                .kakao(kakao)
                .google(google)
                .build();
    }

}
