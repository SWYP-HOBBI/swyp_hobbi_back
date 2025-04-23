package swyp.hobbi.swyphobbiback.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {

    private final JavaMailSender mailSender;

    public void sendEmail(String email, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email); // 받는 사람
            helper.setSubject(title); // 제목
            helper.setText(content, true); // HTML true
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송 중 오류 발생", e);
        }

    }

}
