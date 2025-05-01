package swyp.hobbi.swyphobbiback.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.post.dto.PostCreateRequest;
import swyp.hobbi.swyphobbiback.post.dto.PostResponse;
import swyp.hobbi.swyphobbiback.post.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@Slf4j
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("request") PostCreateRequest request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) {
        PostResponse response = postService.create(userDetails, request, imageFiles);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostDetail(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.findPost(postId), HttpStatus.OK);
    }
}
