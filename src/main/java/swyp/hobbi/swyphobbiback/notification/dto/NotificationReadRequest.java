package swyp.hobbi.swyphobbiback.notification.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class NotificationReadRequest {
    List<Long> notificationIds;
}
