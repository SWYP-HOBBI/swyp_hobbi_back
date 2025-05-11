package swyp.hobbi.swyphobbiback.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.mypage.dto.*;
import swyp.hobbi.swyphobbiback.mypage.service.MypageService;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class MypageController {

    private final MypageService mypageService;

    private Long getCurrentUserId() {
        CustomUserDetails userDetails = (CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUserId();
    }

    @GetMapping("/my-page")
    public ResponseEntity<MyPageResponse> getMyPageInfo() {
        Long userId = getCurrentUserId();
        MyPageResponse response = mypageService.getMyPageInfo(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-page/my-modify-page") // 개인정보수정 페이지 조회
    public ResponseEntity<MyModifyPageResponse> getMyModifyPageInfo() {
        Long userId = getCurrentUserId();
        MyModifyPageResponse response = mypageService.getMyModifyPageInfo(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/my-page/update")
    public ResponseEntity<Void> updateMyPageInfo(@RequestBody MyPageUpdateRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        Long userId = userDetails.getUserId();
        mypageService.updateMyPageInfo(userId, request);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/my-page/update/nickname") // 닉네임 변경
    public ResponseEntity<Void> updateNickname(@RequestBody NicknameUpdateRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        Long userId = userDetails.getUserId();

        mypageService.updateNickname(userId, request.getNickname());
        return ResponseEntity.ok().build();

    }

    @PutMapping("/my-page/update/password") // 비밀번호 변경
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdateRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        Long userId = userDetails.getUserId();

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        mypageService.updatePassword(userId, request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/my-page/update/profile-image", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadProfileImage(@RequestPart MultipartFile newImage) {
        Long userId = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUserId();

        String userImageUrl = mypageService.uploadAndSaveProfileImage(userId, newImage);
        return ResponseEntity.ok(userImageUrl);
    }


    @PostMapping("/my-page/validation/nickname")
    public ResponseEntity<NicknameDuplicateResponse> validateNickname(@RequestBody NicknameUpdateRequest request) {

        Boolean exists = mypageService.isNicknameDuplicate(request.getNickname());
        String message = exists ? "이미 존재하는 닉네임입니다." : "사용 가능한 닉네임입니다.";

        NicknameDuplicateResponse response = new NicknameDuplicateResponse(exists, message);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-page/myposts")
    public ResponseEntity<MyPostsScrollResponse> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long lastPostId,
            @RequestParam(defaultValue = "10") int pageSize) {

        Long userId = userDetails.getUserId();
        MyPostsScrollResponse response = mypageService.getMyPosts(userId, lastPostId, pageSize);
        return ResponseEntity.ok(response);
    }

}
