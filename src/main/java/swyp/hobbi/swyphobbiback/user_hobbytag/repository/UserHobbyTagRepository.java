package swyp.hobbi.swyphobbiback.user_hobbytag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.user_hobbytag.domain.UserHobbyTag;

import java.util.List;

@Repository
public interface UserHobbyTagRepository extends JpaRepository<UserHobbyTag, Long> {
    List<UserHobbyTag> findAllByUser_UserId(Long userId);
}
