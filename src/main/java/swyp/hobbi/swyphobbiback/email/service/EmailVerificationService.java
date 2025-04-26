package swyp.hobbi.swyphobbiback.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.email.domain.EmailVerification;
import swyp.hobbi.swyphobbiback.email.repository.EmailVerificationRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailSendService emailSendService;

    public void sendVerificationLink(String email) {

        String token = UUID.randomUUID().toString(); //랜덤 토큰 생성
        //System.out.println("EmailToken >> " + token);
        String link = "http://localhost:8080/api/v1/email/verify?token=" + token; //인증 링크 생성

        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElse(EmailVerification.builder().email(email).build());

        emailVerification.setToken(token);
        emailVerification.setVerified(false);
        emailVerification.setExpiresAt(LocalDateTime.now().plusMinutes(3)); //인증 만료 시간 3분

        emailVerificationRepository.save(emailVerification);

        //인증 이메일 전송
        String title = "[Hobbi] 이메일 인증 요청";
        String content = "<p>아래 링크를 클릭하여 인증을 완료해주세요:</p>"
                + "<a href=\"" + link + "\">이메일 인증하기</a>";
        emailSendService.sendEmail(email, title, content);

    }

}
