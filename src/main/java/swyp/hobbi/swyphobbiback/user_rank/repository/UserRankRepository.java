package swyp.hobbi.swyphobbiback.user_rank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user_rank.domain.UserRank;

import java.util.Optional;

@Repository
public interface UserRankRepository extends JpaRepository<UserRank, Long> {
    Optional<UserRank> findByUser(User user);
    Optional<UserRank> findByUserId(Long userId);
}
