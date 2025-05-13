package swyp.hobbi.swyphobbiback.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swyp.hobbi.swyphobbiback.user.domain.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname); //닉네임 중복 검사
    boolean existsByEmail(String email); //이메일 중복 검사

    Optional<User> findByEmail(String email);

    @Query(value = """
        SELECT u.userId, u.nickname
        FROM User u
        WHERE u.userId IN(:userIds)
        """)
    Map<Long, String> findNicknamesByIds(@Param("userIds") List<Long> userIds);
}
