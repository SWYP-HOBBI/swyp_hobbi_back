package swyp.hobbi.swyphobbiback.common.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long SSE_EMITTER_TIME_OUT = 60 * 1000L; // 1분 타임아웃

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_EMITTER_TIME_OUT);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event()
                    .name(String.valueOf(userId))
                    .data(" connected")
            );
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    public void sendNotification(Long receiverId, String message) {
        SseEmitter emitter = emitters.get(receiverId);

        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("알림")
                        .data(message)
                );
            } catch (IOException e) {
                emitters.remove(receiverId);
            }
        }
    }
}
