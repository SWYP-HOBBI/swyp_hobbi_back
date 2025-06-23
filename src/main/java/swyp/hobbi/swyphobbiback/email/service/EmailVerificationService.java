package swyp.hobbi.swyphobbiback.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.email.domain.EmailVerification;
import swyp.hobbi.swyphobbiback.email.repository.EmailVerificationRepository;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailSendService emailSendService;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendVerificationCode(String email) {
        String key = "email:limit:" + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw new CustomException(ErrorCode.TOO_MANY_REQUESTS);
        }
        redisTemplate.opsForValue().set(key, "1", 30, TimeUnit.SECONDS); // rate limit 기록 (30초 유지)

        String code = generateAlphaNumericCode(); // 인증코드 생성

        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElse(EmailVerification.builder().email(email).build());
        emailVerification.setCode(code);
        emailVerification.setVerified(false);
        emailVerification.setExpiresAt(LocalDateTime.now().plusMinutes(3)); // 유효시간 3분

        emailVerificationRepository.save(emailVerification);

        // 인증 이메일 전송
        String title = "[Hobbi] 이메일 인증 코드";
        String content = "<p>아래 인증 코드를 입력해주세요:</p>"
                + "<h2>" + code + "</h2>"
                + "<p>인증 코드는 3분간 유효합니다.</p>";

        emailSendService.sendEmail(email, title, content);
    }

    @Transactional
    public void verifyCode(String email, String inputCode) {
        EmailVerification verification = emailVerificationRepository.findByEmailAndCode(email, inputCode)
                .filter(ev -> ev.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_EMAIL_CODE));

        verification.setVerified(true);
        emailVerificationRepository.save(verification);

        emailVerificationRepository.deleteByEmail(email);
        redisTemplate.delete("email:limit:" + email);
    }

    public String generateAlphaNumericCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return code.toString();
    }

}
