package swyp.hobbi.swyphobbiback.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChallengeCacheResponse {
    private ChallengeStatusResponse challenge1;
    private ChallengeStatusResponse challenge2;
    private ChallengeStatusResponse challenge3;
    private Long remainedSeconds;

    @Data
    @AllArgsConstructor
    public static class ChallengeStatusResponse {
        private boolean started;
        private boolean achieved;
        private int point;
    }

    public static ChallengeCacheResponse from(ChallengeCache cache) {
        return new ChallengeCacheResponse(
                new ChallengeStatusResponse(cache.isChallenge1Started(), cache.isChallenge1Achieved(), cache.getChallenge1Point()),
                new ChallengeStatusResponse(cache.isChallenge2Started(), cache.isChallenge2Achieved(), cache.getChallenge2Point()),
                new ChallengeStatusResponse(cache.isChallenge3Started(), cache.isChallenge3Achieved(), cache.getChallenge3Point()),
                cache.getRemainedSeconds()
        );
    }
}
