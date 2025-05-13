package swyp.hobbi.swyphobbiback.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.user.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname); //닉네임 중복 검사
    boolean existsByEmail(String email); //이메일 중복 검사
    boolean existsByEmailAndIsDeletedFalse(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.userHobbyTags uht " +
            "LEFT JOIN FETCH uht.hobbyTag " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithHobbyTags(@Param("email") String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.userHobbyTags uht " +
            "LEFT JOIN FETCH uht.hobbyTag " +
            "WHERE u.userId = :userId")
    Optional<User> findByIdWithHobbyTags(@Param("userId") Long userId);
}
