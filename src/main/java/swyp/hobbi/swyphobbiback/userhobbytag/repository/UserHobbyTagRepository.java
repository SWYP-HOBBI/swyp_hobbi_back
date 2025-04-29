package swyp.hobbi.swyphobbiback.userhobbytag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyp.hobbi.swyphobbiback.userhobbytag.domain.UserHobbyTag;

import java.util.List;

public interface UserHobbyTagRepository extends JpaRepository<UserHobbyTag, Long> {
    List<UserHobbyTag> findAllByUserId(Long userId);
}
