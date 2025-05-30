package swyp.hobbi.swyphobbiback.user_rank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user_rank.domain.UserRank;
import swyp.hobbi.swyphobbiback.user_rank.dto.UserLevelProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRankRepository extends JpaRepository<UserRank, Long> {
    Optional<UserRank> findByUser(User user);

    @Query(value = """
        SELECT ur.user.userId AS userId, ur.level AS userLevel
        FROM UserRank ur
        WHERE ur.user.userId IN(:userIds)
        """)
    List<UserLevelProjection> findUserLevelByUserIds(@Param("userIds") List<Long> userIds);
}
