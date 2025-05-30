package swyp.hobbi.swyphobbiback.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swyp.hobbi.swyphobbiback.notification.domain.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = """
        UPDATE Notification n SET n.readStatus = true
        WHERE n.receiverId = :userId AND n.readStatus = false
        """)
    @Modifying
    void markAllReadByUserId(@Param("userId") Long userId);

    @Query(value = """
        DELETE FROM Notification n
        WHERE n.receiverId = :userId AND n.readStatus = true
        """)
    @Modifying
    void deleteAllReadByUserId(@Param("userId") Long userId);

    @Query(value = """
        SELECT n.notification_id
        FROM notification n
        WHERE n.receiver_id = :userId
        ORDER BY notification_id DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Long> findNotificationIds(@Param("userId") Long userId, @Param("limit") Integer limit);

    @Query(value = """
        SELECT n.notification_id
        FROM notification n
        WHERE n.receiver_id = :userId and n.notification_id < :lastNotificationId
        ORDER BY notification_id DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Long> findNotificationIds(@Param("userId") Long userId, @Param("lastNotificationId") Long lastNotificationId, @Param("limit") Integer limit);

    @Query(value = """
        SELECT DISTINCT n
        FROM Notification n
        WHERE n.notificationId IN(:notificationIds)
        ORDER BY n.notificationId DESC
        """)
    List<Notification> findAllNotifications(@Param("notificationIds") List<Long> notificationIds);
}
