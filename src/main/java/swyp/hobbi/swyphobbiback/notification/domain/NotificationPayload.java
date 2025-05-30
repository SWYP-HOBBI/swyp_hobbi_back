package swyp.hobbi.swyphobbiback.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NotificationPayload {
    private String message;
    private Long unreadCount;
    private NotificationType type;
    private Long targetPostId;
}
