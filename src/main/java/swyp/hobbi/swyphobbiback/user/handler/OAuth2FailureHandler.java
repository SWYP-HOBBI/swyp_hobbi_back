package swyp.hobbi.swyphobbiback.user.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import swyp.hobbi.swyphobbiback.user.dto.OAuth2LoginResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        System.out.println("소셜 로그인 실패: " + exception.getMessage());

        String message = null;
        String providerEmail = null;
        String provider = null;

        if (exception instanceof OAuth2AuthenticationException oauthEx) {
            String errorCode = oauthEx.getError().getErrorCode();

            if ("USER_NOT_REGISTERED".equals(errorCode)) {
                message = "연동된 계정이 없습니다.";

                // 소셜 정보 세션에 저장
                providerEmail = (String) request.getAttribute("socialEmail");
                provider = (String) request.getAttribute("socialProvider");

                if (providerEmail != null && provider != null) {
                    request.getSession().setAttribute("pendingSocialEmail", providerEmail);
                    request.getSession().setAttribute("pendingSocialProvider", provider);
                }

            }
        }

        OAuth2LoginResponse failureResponse = new OAuth2LoginResponse(message, null);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(failureResponse));
    }
}
