package swyp.hobbi.swyphobbiback.user.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import swyp.hobbi.swyphobbiback.token.dto.TokenPair;
import swyp.hobbi.swyphobbiback.token.service.TokenService;
import swyp.hobbi.swyphobbiback.user.domain.OauthLogin;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.dto.LoginResponse;
import swyp.hobbi.swyphobbiback.user.repository.OAuthLoginRepository;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final OAuthLoginRepository oAuthLoginRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");

        log.info("[소셜 로그인 성공] provider={}, email={}", provider, email);
        log.info("[소셜 로그인 성공] user attributes: {}", oAuth2User.getAttributes());

        OauthLogin oauthLogin = oAuthLoginRepository.findByProviderEmailAndProvider(email, provider)
                .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error("USER_NOT_REGISTERED", "연동되지 않은 사용자입니다.", null)));
        User user = userRepository.findByUserIdAndIsDeletedFalse(oauthLogin.getUser().getUserId())
                .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error("USER_NOT_FOUND", "사용자 정보가 없습니다.", null)));

        TokenPair tokens = tokenService.generateAndSaveTokens(user.getEmail()); // 토큰 생성 및 저장

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .userId(oauthLogin.getUser().getUserId())
                .build();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), loginResponse);

    }
}
