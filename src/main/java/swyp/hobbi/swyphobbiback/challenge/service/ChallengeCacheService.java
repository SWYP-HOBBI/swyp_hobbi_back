package swyp.hobbi.swyphobbiback.challenge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.challenge.domain.ChallengeDefaults;
import swyp.hobbi.swyphobbiback.challenge.dto.ChallengeCache;
import swyp.hobbi.swyphobbiback.challenge.repository.ChallengeRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChallengeCacheService {
    private final RedisTemplate<String, ChallengeCache> redisTemplate;
    private final ChallengeRepository challengeRepository;

    private String getKey(Long userId, LocalDateTime weekStart) {
        return ChallengeDefaults.CHALLENGE_KEY_FORMAT
                .formatted(userId, weekStart.toLocalDate());
    }

    public ChallengeCache getCache(Long userId, LocalDateTime weekStart) {
        String key = getKey(userId, weekStart);
        return redisTemplate.opsForValue().get(key);
    }

    public void updateCache(Long userId, LocalDateTime weekStart, ChallengeCache cache) {
        String key = getKey(userId, weekStart);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekEnd = getEndOfWeek(weekStart);
        Duration duration = Duration.between(now, weekEnd);
        long seconds = Math.max(duration.toSeconds(), 0);
        redisTemplate.opsForValue().set(key, cache, seconds, TimeUnit.SECONDS);
    }

    public void syncFromDatabase(Long userId, LocalDateTime weekStart) {
        challengeRepository.findByUserIdAndStartedAt(userId, weekStart)
                .ifPresent(challenge-> {
                    updateCache(userId, weekStart, ChallengeCache.from(challenge));
                });
    }

    public void invalidateCache(Long userId, LocalDateTime weekStart) {
        redisTemplate.delete(getKey(userId, weekStart));
    }

    private LocalDateTime getEndOfWeek(LocalDateTime weekStart) {
        return weekStart.plusDays(ChallengeDefaults.SIX_DAYS).with(LocalTime.MAX);
    }
}
