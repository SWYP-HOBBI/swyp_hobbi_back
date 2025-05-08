package swyp.hobbi.swyphobbiback.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.comment.dto.CommentCreateRequest;
import swyp.hobbi.swyphobbiback.comment.dto.CommentResponse;
import swyp.hobbi.swyphobbiback.comment.dto.CommentUpdateRequest;
import swyp.hobbi.swyphobbiback.comment.service.CommentService;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = commentService.create(userDetails, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request
    ) {
        CommentResponse response = commentService.update(userDetails, commentId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponse>> getAllComments(
            @RequestParam(value = "postId") Long postId,
            @RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize
    ) {
        List<CommentResponse> responses = commentService.getCommentsInfiniteScroll(postId, lastCommentId, pageSize);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId
    ) {
        commentService.deleteLogicOnly(userDetails, commentId);
        return ResponseEntity.ok().build();
    }
}
