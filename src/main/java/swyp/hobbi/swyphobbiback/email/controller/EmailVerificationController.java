package swyp.hobbi.swyphobbiback.email.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.email.domain.EmailVerification;
import swyp.hobbi.swyphobbiback.email.dto.EmailCheckRequest;
import swyp.hobbi.swyphobbiback.email.repository.EmailVerificationRepository;
import swyp.hobbi.swyphobbiback.email.service.EmailVerificationService;
import swyp.hobbi.swyphobbiback.email.dto.EmailRequest;
import swyp.hobbi.swyphobbiback.email.dto.EmailCheckResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final EmailVerificationRepository emailVerificationRepository;

    @PostMapping("/send") //인증 이메일 발송
    public ResponseEntity<String> sendVerificationEmail(@RequestBody EmailRequest request) {
        String email = request.getEmail();
        emailVerificationService.sendVerificationLink(email);
        return ResponseEntity.ok("인증 메일 발송 완료!");
    }

//    @GetMapping("/verify") //토큰으로 인증 처리
//    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
//        EmailVerification verification = emailVerificationRepository.findByToken(token)
//                .orElseThrow(() -> new IllegalArgumentException("잘못된 인증 토큰입니다."));
//
//        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
//            return ResponseEntity.badRequest().body("인증 링크가 만료되었습니다.");
//        }
//
//        verification.setVerified(true);
//        emailVerificationRepository.save(verification);
//
//        return ResponseEntity.ok("이메일 인증이 완료되었습니다!");
//
//    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token,
                                         @RequestParam(required = false) String redirectUrl,
                                         HttpServletResponse response) throws IOException {
        EmailVerification verification = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 인증 토큰입니다."));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            response.sendRedirect("http://localhost:3000/verify_fail");
            return ResponseEntity.ok(response);
        }

        verification.setVerified(true);
        emailVerificationRepository.save(verification);

        String email = verification.getEmail();

        String targetUrl = (redirectUrl != null) ? redirectUrl : "http://localhost:3000/verify_email?token=" + token + "&email=" + email;
        response.sendRedirect(targetUrl);

        log.info("redirectUrl: {}", targetUrl);

        return ResponseEntity.ok(targetUrl);
    }


    @PostMapping("/verification/check") //이메일 인증 여부 확인
    public ResponseEntity<EmailCheckResponse> checkEmailVerification(@RequestBody EmailCheckRequest request) {
        String email = request.getEmail();
        String token = request.getToken();

        boolean isVerified = emailVerificationRepository.findByEmailAndToken(email, token)
                .map(EmailVerification::getVerified)
                .orElse(false);

        String message = isVerified
                ? "이메일 인증이 완료되었습니다."
                : "이메일 인증이 완료되지 않았습니다.";


        return ResponseEntity.ok(new EmailCheckResponse(message));

    }
}
