package swyp.hobbi.swyphobbiback.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChallengeCacheResponse {
    private ChallengeStatusResponse hobbyShowOff;
    private ChallengeStatusResponse hobbyRoutiner;
    private ChallengeStatusResponse hobbyRich;

    @Data
    @AllArgsConstructor
    public static class ChallengeStatusResponse {
        private boolean started;
        private boolean achieved;
        private int point;
    }

    public static ChallengeCacheResponse from(ChallengeCache cache) {
        return new ChallengeCacheResponse(
                new ChallengeStatusResponse(cache.isHobbyShowOffStarted(), cache.isHobbyShowOffAchieved(), cache.getHobbyShowOffPoint()),
                new ChallengeStatusResponse(cache.isHobbyRoutinerStarted(), cache.isHobbyRoutinerAchieved(), cache.getHobbyRoutinerPoint()),
                new ChallengeStatusResponse(cache.isHobbyRichStarted(), cache.isHobbyRichAchieved(), cache.getHobbyRichPoint())
        );
    }
}
