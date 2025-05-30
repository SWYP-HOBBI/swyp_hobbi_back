package swyp.hobbi.swyphobbiback.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import swyp.hobbi.swyphobbiback.notification.domain.NotificationPayload;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long SSE_EMITTER_TIME_OUT = 30 * 60 * 1000L; // 30분 타임아웃

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_EMITTER_TIME_OUT);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            emitters.remove(userId);
        });
        emitter.onTimeout(() -> {
            emitters.remove(userId);
        });
        emitter.onError(e -> {
            emitters.remove(userId);
        });

        try {
            emitter.send(SseEmitter.event()
                    .name(String.valueOf(userId))
                    .data(" connected")
            );
        } catch (IOException e) {
            log.error("[SSE] {} - 최초 응답 전송 실패: {}", userId, e.getMessage());
            emitters.remove(userId);
        }

        return emitter;
    }

    public void sendNotification(Long receiverId, NotificationPayload payload) {
        SseEmitter emitter = emitters.get(receiverId);

        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(payload)
                );
            } catch (IOException e) {
                log.error("알림 보내기 에러 : {}", e.getMessage());
                emitters.remove(receiverId);
            }
        }
    }
}
