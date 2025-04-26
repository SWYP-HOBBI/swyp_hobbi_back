package swyp.hobbi.swyphobbiback.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swyp.hobbi.swyphobbiback.user.dto.*;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;
import swyp.hobbi.swyphobbiback.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

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


}
