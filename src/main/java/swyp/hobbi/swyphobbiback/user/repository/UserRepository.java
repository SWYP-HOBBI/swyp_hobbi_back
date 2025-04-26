package swyp.hobbi.swyphobbiback.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp.hobbi.swyphobbiback.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname); //닉네임 중복 검사
    boolean existsByEmail(String email); //이메일 중복 검사

    Optional<User> findByEmail(String email);
}
