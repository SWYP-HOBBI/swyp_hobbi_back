package swyp.hobbi.swyphobbiback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.email.service.EmailSendService;
import swyp.hobbi.swyphobbiback.user.domain.PasswordResetToken;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.repository.PasswordResetTokenRepository;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static swyp.hobbi.swyphobbiback.common.error.ErrorCode.EXPIRED_TOKEN;
import static swyp.hobbi.swyphobbiback.common.error.ErrorCode.INVALID_TOKEN;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailSendService emailSendService;

    public void sendPasswordResetLink(String email) {

        if (!userRepository.existsByEmailAndIsDeletedFalse(email)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        String token = UUID.randomUUID().toString(); // 토큰 생성
        String link = "http://hobbi.co.kr/api/v1/user/password/verify?token=" + token;

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(3)) // 3분 후 만료
                .verified(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        String title = "[Hobbi] 비밀번호 재설정 위한 이메일 인증";
        String content = "<p>아래 링크를 클릭하여 이메일을 인증해주세요 :</p>"
                + "<a href=\"" + link + "\">비밀번호 재설정</a>";

        emailSendService.sendEmail(email, title, content); // 인증 이메일 전송
    }

    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(INVALID_TOKEN));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) { //만료된 토큰일 경우
            throw new CustomException(EXPIRED_TOKEN);
        }


        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(newPassword)); // 비밀번호 암호화 후 변경
        userRepository.save(user);

        resetToken.setVerified(true); // 토큰 사용 처리
        passwordResetTokenRepository.save(resetToken); // 토큰 상태 업데이트
    }
}
