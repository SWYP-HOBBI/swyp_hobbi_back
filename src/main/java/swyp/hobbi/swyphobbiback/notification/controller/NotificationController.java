package swyp.hobbi.swyphobbiback.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.notification.dto.NotificationReadRequest;
import swyp.hobbi.swyphobbiback.notification.dto.NotificationResponse;
import swyp.hobbi.swyphobbiback.notification.service.NotificationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "lastNotificationId", required = false) Long lastNotificationId,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize
    ) {
        List<NotificationResponse> responses = notificationService.getAllNotifications(userDetails, lastNotificationId, pageSize);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotificationAndDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long notificationId
    ) {
        NotificationResponse response = notificationService.getNotificationAndDetails(userDetails, notificationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long unreadCount = notificationService.getUnreadCount(userDetails.getUserId());
        return ResponseEntity.ok(unreadCount);
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllAsRead(userDetails);
        return ResponseEntity.ok().build();
    }

    @PostMapping("read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody NotificationReadRequest request
    ) {
        notificationService.markAsRead(userDetails, request);
        return ResponseEntity.ok().build();
    }
}
