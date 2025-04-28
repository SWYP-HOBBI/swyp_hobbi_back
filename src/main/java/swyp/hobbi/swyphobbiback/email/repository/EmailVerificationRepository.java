package swyp.hobbi.swyphobbiback.email.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp.hobbi.swyphobbiback.email.domain.EmailVerification;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByToken(String token);
    Optional<EmailVerification> findByEmail(String email);
    Optional<EmailVerification> findByEmailAndToken(String email, String token);
}
