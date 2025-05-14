package swyp.hobbi.swyphobbiback.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.UserNotFoundException;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.common.service.SseService;
import swyp.hobbi.swyphobbiback.notification.domain.Notification;
import swyp.hobbi.swyphobbiback.notification.domain.NotificationCount;
import swyp.hobbi.swyphobbiback.notification.domain.NotificationType;
import swyp.hobbi.swyphobbiback.notification.dto.NotificationReadRequest;
import swyp.hobbi.swyphobbiback.notification.dto.NotificationResponse;
import swyp.hobbi.swyphobbiback.notification.repository.NotificationCountRepository;
import swyp.hobbi.swyphobbiback.notification.repository.NotificationRepository;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.dto.NicknameProjection;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationCountRepository notificationCountRepository;
    private final UserRepository userRepository;
    private final SseService sseService;

    public void sendNotification(Long receiverId, Long senderId, String message, NotificationType type, Long targetPostId) {
        Notification notification = Notification.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .notificationType(type)
                .targetPostId(targetPostId)
                .readStatus(false)
                .message(message)
                .build();

        notificationRepository.save(notification);

        int result = notificationCountRepository.increase(receiverId);
        if(result == 0) {
            notificationCountRepository.save(
                    NotificationCount.init(receiverId, 1L)
            );
        }

        Long notificationCount = getUnreadCount(receiverId);

        sseService.sendNotification(receiverId, String.valueOf(notificationCount));
        sseService.sendNotification(receiverId, message);
    }
    
    public List<NotificationResponse> getAllNotifications(CustomUserDetails userDetails, Long lastNotificationId, Integer pageSize) {
        Long userId = userDetails.getUserId();

        List<Long> notificationIds = fetchNotificationIds(userId, pageSize, lastNotificationId);
        List<Notification> notifications = notificationRepository.findAllNotifications(notificationIds);
        List<Long> senderIds = notifications.stream()
                .map(Notification::getSenderId)
                .toList();
        Map<Long, String> senderNicknames = userRepository.findNicknamesByIds(senderIds).stream()
                .collect(Collectors.toMap(NicknameProjection::getUserId, NicknameProjection::getNickname));

        return notifications.stream()
                .map(notification -> {
                    String senderNickname = senderNicknames.get(notification.getSenderId());
                    return NotificationResponse.from(notification, senderNickname);
                })
                .toList();
    }

    private List<Long> fetchNotificationIds(Long userId, Integer pageSize, Long lastNotificationId) {
        boolean isFirstPage = lastNotificationId == null || lastNotificationId == 0;
        if(isFirstPage) {
            return notificationRepository.findNotificationIds(userId, pageSize);
        } else {
            return notificationRepository.findNotificationIds(userId, lastNotificationId, pageSize);
        }
    }

    @Transactional
    public NotificationResponse getNotificationAndDetails(CustomUserDetails userDetails, Long notificationId) {
        Long userId = userDetails.getUserId();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow();

        if(!notification.getReceiverId().equals(userId)) {
            throw new AccessDeniedException(ErrorCode.FORBIDDEN.getMessage());
        }

        notification.setReadTrue();
        notificationCountRepository.decrease(userId);

        User sender = userRepository.findById(notification.getSenderId())
                .orElseThrow(UserNotFoundException::new);

        notificationRepository.delete(notification);

        sseService.sendNotification(notification.getReceiverId(), String.valueOf(getUnreadCount(userId)));

        return NotificationResponse.from(notification, sender.getNickname());
    }

    public Long getUnreadCount(Long userId) {
        return notificationCountRepository.findById(userId)
                .map(NotificationCount::getUnreadCount)
                .orElse(0L);
    }

    @Transactional
    public void markAllAsRead(CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        if(notificationCountRepository.findById(userId).isPresent()) {
            notificationRepository.markAllReadByUserId(userId);
            notificationRepository.deleteAllReadByUserId(userId);

            NotificationCount notificationCount = notificationCountRepository.findById(userId)
                    .orElseThrow();
            notificationCount.initUnreadCount();

            sseService.sendNotification(userId, String.valueOf(notificationCount.getUnreadCount()));
        }
    }

    @Transactional
    public void markAsRead(CustomUserDetails userDetails, NotificationReadRequest request) {
        Long userId = userDetails.getUserId();
        List<Long> notificationIds = request.getNotificationIds();
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);

        notifications.stream()
                .filter(notification -> notification.getReceiverId().equals(userId))
                .forEach(notification -> {
                    notification.setReadTrue();
                    notificationCountRepository.decrease(userId);
                    notificationRepository.delete(notification);
                });

        sseService.sendNotification(userId, String.valueOf(getUnreadCount(userId)));
    }
}
