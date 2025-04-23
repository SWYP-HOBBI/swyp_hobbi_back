package swyp.hobbi.swyphobbiback.email.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.email.domain.EmailVerification;
import swyp.hobbi.swyphobbiback.email.repository.EmailVerificationRepository;
import swyp.hobbi.swyphobbiback.email.service.EmailVerificationService;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final EmailVerificationRepository emailVerificationRepository;

    @PostMapping("/send")
    public ResponseEntity<String> sendVerificationEmail(@RequestParam String email) {
        emailVerificationService.sendVerificationLink(email);
        return ResponseEntity.ok("인증 메일 발송 완료!");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        EmailVerification verification = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 인증 토큰입니다."));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("인증 링크가 만료되었습니다.");
        }

        verification.setVerified(true);
        emailVerificationRepository.save(verification);

        return ResponseEntity.ok("이메일 인증이 완료되었습니다!");
    }
}
