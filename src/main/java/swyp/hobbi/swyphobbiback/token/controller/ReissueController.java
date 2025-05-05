package swyp.hobbi.swyphobbiback.token.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swyp.hobbi.swyphobbiback.common.security.JwtTokenProvider;

@RestController
@RequestMapping("api/v1/token")
@RequiredArgsConstructor
public class ReissueController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.getRefreshTokenFromCookies(request);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(email);

        ResponseCookie newAccessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(30 * 60) // 30분
                .sameSite("Lax")
                .build();

        response.setHeader("Set-Cookie", newAccessTokenCookie.toString());

        return ResponseEntity.ok().build();
    }

}
