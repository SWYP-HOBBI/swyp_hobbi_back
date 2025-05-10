package swyp.hobbi.swyphobbiback.like.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.like.dto.LikeResponse;
import swyp.hobbi.swyphobbiback.like.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/like/post/{postId}")
    public ResponseEntity<LikeResponse> like(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        LikeResponse response = likeService.likeOptimisticLock(userDetails, postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unlike/post/{postId}")
    public ResponseEntity<LikeResponse> unlike(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        LikeResponse response = likeService.unlikeOptimisticLock(userDetails, postId);
        return ResponseEntity.ok(response);
    }
}
