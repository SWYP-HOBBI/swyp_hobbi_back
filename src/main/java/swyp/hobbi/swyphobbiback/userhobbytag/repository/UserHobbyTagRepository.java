package swyp.hobbi.swyphobbiback.userhobbytag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.userhobbytag.domain.UserHobbyTag;

import java.util.List;

@Repository
public interface UserHobbyTagRepository extends JpaRepository<UserHobbyTag, Long> {
    List<UserHobbyTag> findAllByUser_UserId(Long userId);
}
