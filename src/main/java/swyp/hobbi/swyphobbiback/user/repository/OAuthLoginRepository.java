package swyp.hobbi.swyphobbiback.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.user.domain.OauthLogin;

import java.util.Optional;

@Repository
public interface OAuthLoginRepository extends JpaRepository<OauthLogin, Long> {
    Optional<OauthLogin> findByProviderEmailAndProvider(String providerEmail, String provider);
    Boolean existsByProviderEmailAndProvider(String socialEmail, String provider);
    Boolean existsByUserUserIdAndProvider(Long userId, String provider);
}
