package swyp.hobbi.swyphobbiback.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.user.dto.LoginResponse;
import swyp.hobbi.swyphobbiback.user.dto.OauthLoginStatusResponse;
import swyp.hobbi.swyphobbiback.user.service.OAuthService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
@Slf4j
public class OAuthController {

    private final OAuthService oauthService;

    @GetMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> oauthLogin(@PathVariable String provider, @RequestParam String code) {
        return ResponseEntity.ok(oauthService.handleOAuthLogin(provider, code));
    }

    @PostMapping("/link") // 소셜 계정 연동
    public ResponseEntity<?> linkSocialAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            oauthService.linkSocialAccount(userDetails.getUserId());
            return ResponseEntity.ok(Map.of("message", "소셜 계정이 성공적으로 연동되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<OauthLoginStatusResponse> getSocialLinkStatus(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(oauthService.getLinkStatus(userDetails.getUserId()));
    }
}
