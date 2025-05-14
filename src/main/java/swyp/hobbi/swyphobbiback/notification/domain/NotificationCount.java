package swyp.hobbi.swyphobbiback.notification.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "notification_count")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder
public class NotificationCount {
    @Id
    private Long userId;
    private Long unreadCount;

    public static NotificationCount init(Long userId, Long unreadCount) {
        NotificationCount notificationCount = new NotificationCount();
        notificationCount.userId = userId;
        notificationCount.unreadCount = unreadCount;

        return notificationCount;
    }

    public void increase() {
        this.unreadCount++;
    }

    public void decrease() {
        this.unreadCount--;
    }

    public void initUnreadCount() {
        this.unreadCount = 0L;
    }
}
