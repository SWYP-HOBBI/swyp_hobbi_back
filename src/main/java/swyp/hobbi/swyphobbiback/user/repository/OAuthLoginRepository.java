package swyp.hobbi.swyphobbiback.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.user.domain.OauthLogin;

import java.util.Optional;

@Repository
public interface OAuthLoginRepository extends JpaRepository<OauthLogin, Long> {
    Optional<OauthLogin> findByProviderEmailAndProvider(String providerEmail, String provider);
    boolean existsByProviderEmailAndProvider(String socialEmail, String provider);
}
