package swyp.hobbi.swyphobbiback.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.user.service.OAuthService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuthController {

    private final OAuthService oauthService;

    @GetMapping("/session") //세션 확인
    public ResponseEntity<?> getPendingSocialInfo(HttpSession session) {
        String email = (String) session.getAttribute("pendingSocialEmail");
        String provider = (String) session.getAttribute("pendingSocialProvider");

        if (email != null && provider != null) {
            return ResponseEntity.ok(Map.of(
                    "email", email,
                    "provider", provider
            ));
        }

        return ResponseEntity.noContent().build(); // 세션에 없으면 204 응답
    }

    @PostMapping("/link") // 소셜 계정 연동
    public ResponseEntity<?> linkSocialAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpSession session
    ) {
        try {
            oauthService.linkSocialAccount(userDetails.getUserId(), session);
            return ResponseEntity.ok(Map.of("message", "소셜 계정이 성공적으로 연동되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

}
