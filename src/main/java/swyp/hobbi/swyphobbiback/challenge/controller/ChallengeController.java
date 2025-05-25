package swyp.hobbi.swyphobbiback.challenge.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.challenge.dto.ChallengeCacheResponse;
import swyp.hobbi.swyphobbiback.challenge.service.ChallengeService;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenge")
public class ChallengeController {
    private final ChallengeService challengeService;

    @PostMapping("/start/{challengeNumber}")
    public ResponseEntity<Void> startChallenge(@PathVariable int challengeNumber, @AuthenticationPrincipal CustomUserDetails userDetails) {
        challengeService.startSpecificChallenge(userDetails.getUserId(), challengeNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<ChallengeCacheResponse> getAllChallenges(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ChallengeCacheResponse challengeCache = challengeService.getChallengeCache(userDetails.getUserId());
        return ResponseEntity.ok(challengeCache);
    }
}
