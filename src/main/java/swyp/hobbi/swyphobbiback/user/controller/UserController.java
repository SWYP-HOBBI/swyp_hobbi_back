package swyp.hobbi.swyphobbiback.user.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.token.repository.RefreshTokenRepository;
import swyp.hobbi.swyphobbiback.user.dto.*;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;
import swyp.hobbi.swyphobbiback.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/validation/email") // 이메일 중복 검증
    public ResponseEntity<DuplicateCheckResponse> validateEmail(@RequestBody EmailCheckRequest emailCheckRequest) {
        boolean exists = userRepository.existsByEmail(emailCheckRequest.getEmail());
        return ResponseEntity.ok(new DuplicateCheckResponse(exists));
    }

    @PostMapping ("/validation/nickname") // 닉네임 중복 검증
    public ResponseEntity<DuplicateCheckResponse> validateNickname(@RequestBody NicknameCheckRequest nicknameCheckRequest) {
        boolean exists = userRepository.existsByNickname(nicknameCheckRequest.getNickname());
        return ResponseEntity.ok(new DuplicateCheckResponse(exists));
    }

    @PostMapping ("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody UserCreateRequest userCreateRequest) {
        UserResponse userResponse = userService.signUp(userCreateRequest);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping ("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/delete")
    public ResponseEntity<UserDeleteResponse> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestBody UserDeleteRequest request) {
        Long userId = userDetails.getUserId();
        userService.delete(userId, request);

        return ResponseEntity.ok(new UserDeleteResponse("회원 탈퇴가 완료되었습니다."));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getEmail();
        refreshTokenRepository.deleteByEmail(email); // DB에 저장된 refreshToken 삭제

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

}