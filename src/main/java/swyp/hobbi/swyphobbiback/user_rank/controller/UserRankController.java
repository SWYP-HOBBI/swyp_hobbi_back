package swyp.hobbi.swyphobbiback.user_rank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.user_rank.dto.UserRankResponseDto;
import swyp.hobbi.swyphobbiback.user_rank.service.UserRankService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-rank")
public class UserRankController {

    private final UserRankService userRankService;

    @GetMapping("/me")
    public ResponseEntity<UserRankResponseDto> getMyRank(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserRankResponseDto responseDto = userRankService.getUserRank(userDetails.getUser());
        return ResponseEntity.ok(responseDto);
    }
}