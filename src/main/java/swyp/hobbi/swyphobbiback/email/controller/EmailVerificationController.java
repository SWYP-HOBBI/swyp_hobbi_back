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
import java.util.Optional;

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

    @GetMapping("/verify")
    public void verifyEmail(@RequestParam String token,
                                         @RequestParam(required = false) String redirectUrl,
                                         HttpServletResponse response) throws IOException {

        Optional<EmailVerification> optionalVerification = emailVerificationRepository.findByToken(token);

        //유효하지 않은 토큰이거나 만료된 토큰일 경우
        if (optionalVerification.isEmpty() ||
                optionalVerification.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            response.sendRedirect("http://swyp-hobbi-front.vercel.app/verify_fail");
            return;
        }

        EmailVerification verification = optionalVerification.get();
        verification.setVerified(true);
        emailVerificationRepository.save(verification);

        String email = verification.getEmail();
        String targetUrl = (redirectUrl != null) ? redirectUrl : "http://swyp-hobbi-front.vercel.app/verify_email?token=" + token + "&email=" + email;

        response.sendRedirect(targetUrl);
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
