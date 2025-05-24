package swyp.hobbi.swyphobbiback.challenge.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swyp.hobbi.swyphobbiback.challenge.service.ChallengeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenge")
public class ChallengeController {
    private final ChallengeService challengeService;
}
