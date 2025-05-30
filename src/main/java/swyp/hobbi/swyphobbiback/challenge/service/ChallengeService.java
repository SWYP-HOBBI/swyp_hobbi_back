package swyp.hobbi.swyphobbiback.challenge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.challenge.domain.Challenge;
import swyp.hobbi.swyphobbiback.challenge.domain.ChallengeDefaults;
import swyp.hobbi.swyphobbiback.challenge.dto.ChallengeCache;
import swyp.hobbi.swyphobbiback.challenge.dto.ChallengeCacheResponse;
import swyp.hobbi.swyphobbiback.challenge.repository.ChallengeRepository;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.user_rank.service.UserRankService;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeCacheService challengeCacheService;
    private final UserRankService userRankService;

    public ChallengeCacheResponse getChallengeCache(Long userId) {
        LocalDateTime weekStart = getStartOfWeek(LocalDateTime.now());
        ChallengeCache cache = challengeCacheService.getCache(userId, weekStart);

        if(cache == null) {
            Challenge challenge = challengeRepository.findByUserIdAndStartedAt(userId, weekStart)
                    .orElseGet(() -> createChallenge(userId, weekStart));
            challengeCacheService.updateCache(userId, weekStart, ChallengeCache.from(challenge));
            cache = challengeCacheService.getCache(userId, weekStart);
        }

        return ChallengeCacheResponse.from(cache);
    }

    public void startSpecificChallenge(Long userId, int challengeNumber) {
        LocalDateTime weekStart = getStartOfWeek(LocalDateTime.now());
        Challenge challenge = challengeRepository.findByUserIdAndStartedAt(userId, weekStart)
                .orElseGet(() -> createChallenge(userId, weekStart));

        switch (challengeNumber) {
            case 1 -> challenge.setChallenge1Started(true);
            case 2 -> challenge.setChallenge2Started(true);
            case 3 -> challenge.setChallenge3Started(true);
            default -> throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        challengeRepository.save(challenge);
        challengeCacheService.updateCache(userId, weekStart, ChallengeCache.from(challenge));
    }

    public void evaluateChallenges(Long userId) {
        LocalDateTime weekStart = getStartOfWeek(LocalDateTime.now());
        LocalDateTime weekEnd = getEndOfWeek(weekStart);
        Challenge challenge = challengeRepository.findByUserIdAndStartedAt(userId, weekStart)
                .orElse(null);

        if(challenge == null) {
            return;
        }

        // 취미 자랑 챌린지 - 좋아요 누적 50개
        if(challenge.getChallenge1Started() && !challenge.getChallenge1Achieved()) {
            Integer likeCount = challengeRepository.countLikeThisWeek(userId, weekStart, weekEnd);
            challenge.setChallenge1Point(likeCount);

            if(likeCount >= ChallengeDefaults.LIKE_STACK_COUNT) {
                challenge.setChallenge1Achieved(true);
                userRankService.addExp(userId, 10);
            }
        }

        // 루티너 챌린지 - 같은 취미 게시글 3개
        if(challenge.getChallenge2Started() && !challenge.getChallenge2Achieved()) {
            Integer count = challengeRepository.countPostsWithSameTagsThisWeek(userId, weekStart, weekEnd);
            challenge.setChallenge2Point(count);

            if (count >= ChallengeDefaults.POST_SAME_TAGS_COUNT) {
                challenge.setChallenge2Achieved(true);
                userRankService.addExp(userId, 10);
            }
        }

        // 취미 부자 챌린지 - 다른 취미 게시글 3개
        if(challenge.getChallenge3Started() && !challenge.getChallenge3Achieved()) {
            Integer count = challengeRepository.countPostsWithDiffTagsThisWeek(userId, weekStart, weekEnd);
            challenge.setChallenge3Point(count);

            if (count >= ChallengeDefaults.POST_DIFF_TAGS_COUNT) {
                challenge.setChallenge3Achieved(true);
                userRankService.addExp(userId, 10);
            }
        }

        challenge.setRemainedSeconds(calculateRemainedAt(weekStart).getSeconds());
        challengeRepository.save(challenge);
        challengeCacheService.updateCache(userId, weekStart, ChallengeCache.from(challenge));
    }

    private LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate()
                .atStartOfDay();
    }

    private LocalDateTime getEndOfWeek(LocalDateTime weekStart) {
        return weekStart.plusDays(ChallengeDefaults.SIX_DAYS).with(LocalTime.MAX);
    }

    private Challenge createChallenge(Long userId, LocalDateTime weekStart) {
        Challenge challenge = Challenge.builder()
                .userId(userId)
                .startedAt(weekStart)
                .remainedSeconds(calculateRemainedAt(weekStart).getSeconds())
                .challenge1Point(ChallengeDefaults.ZERO)
                .challenge2Point(ChallengeDefaults.ZERO)
                .challenge3Point(ChallengeDefaults.ZERO)
                .build();

        return challengeRepository.save(challenge);
    }

    private Duration calculateRemainedAt(LocalDateTime weekStart) {
        LocalDateTime weekEnd = getEndOfWeek(weekStart);
        LocalDateTime now = LocalDateTime.now();

        return Duration.between(now, weekEnd);
    }
}
