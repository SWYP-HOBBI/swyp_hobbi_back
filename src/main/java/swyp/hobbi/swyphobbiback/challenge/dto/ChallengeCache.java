package swyp.hobbi.swyphobbiback.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swyp.hobbi.swyphobbiback.challenge.domain.Challenge;

import java.io.Serializable;
import java.time.Duration;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeCache implements Serializable {
    private int challenge1Point;
    private int challenge2Point;
    private int challenge3Point;
    private boolean challenge1Started;
    private boolean challenge2Started;
    private boolean challenge3Started;
    private boolean challenge1Achieved;
    private boolean challenge2Achieved;
    private boolean challenge3Achieved;

    public static ChallengeCache from(Challenge challenge) {
        ChallengeCache cache = new ChallengeCache();
        cache.challenge1Point = challenge.getChallenge1Point();
        cache.challenge2Point = challenge.getChallenge2Point();
        cache.challenge3Point = challenge.getChallenge3Point();
        cache.challenge1Started = challenge.getChallenge1Started();
        cache.challenge2Started = challenge.getChallenge2Started();
        cache.challenge3Started = challenge.getChallenge3Started();
        cache.challenge1Achieved = challenge.getChallenge1Achieved();
        cache.challenge2Achieved = challenge.getChallenge2Achieved();
        cache.challenge3Achieved = challenge.getChallenge3Achieved();

        return cache;
    }
}
