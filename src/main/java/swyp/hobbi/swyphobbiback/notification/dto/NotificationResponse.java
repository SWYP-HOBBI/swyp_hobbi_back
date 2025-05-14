package swyp.hobbi.swyphobbiback.notification.dto;

import lombok.Getter;
import swyp.hobbi.swyphobbiback.notification.domain.Notification;

import java.time.LocalDateTime;

@Getter
public class NotificationResponse {
    private Long notificationId;
    private Long receiverId;
    private Long targetPostId;
    private String senderNickname;
    private String message;
    private String notificationType;
    private Boolean read;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification, String senderNickname) {
        NotificationResponse response = new NotificationResponse();
        response.notificationId = notification.getNotificationId();
        response.receiverId = notification.getReceiverId();
        response.targetPostId = notification.getTargetPostId();
        response.senderNickname = senderNickname;
        response.message = notification.getMessage();
        response.notificationType = notification.getNotificationType().toString();
        response.read = notification.getReadStatus();
        response.createdAt = notification.getCreatedAt();

        return response;
    }
}
