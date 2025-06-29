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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailSendService emailSendService;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

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
        emailSendService.sendEmail(email, EmailContentBuilder.getVerificationTitle(), EmailContentBuilder.buildVerificationContent(code));
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
        Random rnd = new Random();
        return rnd.ints(6, 0, CHARACTERS.length())
                .mapToObj(i -> String.valueOf(CHARACTERS.charAt(i)))
                .collect(Collectors.joining());
    }

}
