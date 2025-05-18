package swyp.hobbi.swyphobbiback.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.email.dto.EmailCheckRequest;
import swyp.hobbi.swyphobbiback.email.dto.EmailCheckResponse;
import swyp.hobbi.swyphobbiback.user.domain.PasswordResetToken;
import swyp.hobbi.swyphobbiback.user.dto.PasswordResetEmailRequest;
import swyp.hobbi.swyphobbiback.user.dto.PasswordResetRequest;
import swyp.hobbi.swyphobbiback.user.repository.PasswordResetTokenRepository;
import swyp.hobbi.swyphobbiback.user.service.PasswordResetService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user/password")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @PostMapping("/reset-link") // 인증 이메일 전송
    public ResponseEntity<String> sendResetLink(@RequestBody PasswordResetEmailRequest request) {
        passwordResetService.sendPasswordResetLink(request.getEmail());
        return ResponseEntity.ok("인증 메일 발송 완료!");
    }

    @GetMapping("/verify") // 토큰 검증 후 리다이렉트
    public void verifyToken(@RequestParam String token,
                            @RequestParam(required = false) String redirectUrl,
                            HttpServletResponse response) throws IOException {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (!passwordResetService.validateToken(token)) {
            response.sendRedirect("http://swyp-hobbi-front.vercel.app/verify_fail");
            return;
        }

        resetToken.setVerified(true);
        passwordResetTokenRepository.save(resetToken);

        String email = resetToken.getEmail();
        String targetUrl = (redirectUrl != null) ? redirectUrl : "http://swyp-hobbi-front.vercel.app/verify_email?token=" + token + "&email=" + email + "&reset=true";

        log.info("redirectUrl: {}", targetUrl);
        response.sendRedirect(targetUrl);
    }

    @PostMapping("/verify/check") //이메일 인증 여부 확인
    public ResponseEntity<EmailCheckResponse> checkEmailVerification(@RequestBody EmailCheckRequest request) {
        String email = request.getEmail();
        String token = request.getToken();

        boolean isVerified = passwordResetTokenRepository.findByEmailAndToken(email, token)
                .map(PasswordResetToken::getVerified)
                .orElse(false);

        String message = isVerified
                ? "이메일 인증이 완료되었습니다."
                : "이메일 인증이 완료되지 않았습니다.";

        return ResponseEntity.ok(new EmailCheckResponse(message));

    }

    @PostMapping("/reset") // 비밀번호 변경
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}
