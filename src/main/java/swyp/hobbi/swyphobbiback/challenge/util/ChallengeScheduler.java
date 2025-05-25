package swyp.hobbi.swyphobbiback.challenge.util;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import swyp.hobbi.swyphobbiback.challenge.domain.Challenge;
import swyp.hobbi.swyphobbiback.challenge.domain.ChallengeDefaults;
import swyp.hobbi.swyphobbiback.challenge.repository.ChallengeRepository;
import swyp.hobbi.swyphobbiback.challenge.service.ChallengeCacheService;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChallengeScheduler {
    private final ChallengeRepository challengeRepository;
    private final ChallengeCacheService challengeCacheService;

    @Scheduled(cron = "0 0 0 * * MON")
    public void resetChallenges() {
        LocalDateTime weekStart = getStartOfWeek(LocalDateTime.now());
        List<Challenge> oldChallenges = challengeRepository.findByStartedAtBefore(weekStart);

        for(Challenge challenge : oldChallenges){
            challenge.setChallenge1Started(false);
            challenge.setChallenge2Started(false);
            challenge.setChallenge3Started(false);
            challenge.setChallenge1Achieved(false);
            challenge.setChallenge2Achieved(false);
            challenge.setChallenge3Achieved(false);
            challenge.setChallenge1Point(0);
            challenge.setChallenge2Point(0);
            challenge.setChallenge3Point(0);
            challenge.setStartedAt(weekStart);
            challenge.setRemainedSeconds(calculateRemainedAt(weekStart).getSeconds());

            challengeRepository.save(challenge);
            challengeCacheService.invalidateCache(challenge.getUserId(), weekStart);
        }
    }

    private LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate()
                .atStartOfDay();
    }

    private LocalDateTime getEndOfWeek(LocalDateTime weekStart) {
        return weekStart.plusDays(ChallengeDefaults.SIX_DAYS).with(LocalTime.MAX);
    }

    private Duration calculateRemainedAt(LocalDateTime weekStart) {
        LocalDateTime weekEnd = getEndOfWeek(weekStart);
        LocalDateTime now = LocalDateTime.now();

        return Duration.between(now, weekEnd);
    }
}
