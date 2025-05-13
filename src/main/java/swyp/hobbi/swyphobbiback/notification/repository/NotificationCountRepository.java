package swyp.hobbi.swyphobbiback.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.notification.domain.NotificationCount;

@Repository
public interface NotificationCountRepository extends JpaRepository<NotificationCount, Long> {
    @Query(value = """
        UPDATE NotificationCount nc SET nc.unreadCount = nc.unreadCount + 1
        WHERE nc.userId = :userId
        """)
    @Modifying
    int increase(@Param("userId") Long userId);

    @Query(value = """
        UPDATE NotificationCount nc SET nc.unreadCount = nc.unreadCount - 1
        WHERE nc.userId = :userId
        """)
    @Modifying
    int decrease(@Param("userId") Long userId);
}
