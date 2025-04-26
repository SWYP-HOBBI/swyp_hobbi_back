package swyp.hobbi.swyphobbiback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.token.dto.TokenPair;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.email.domain.EmailVerification;
import swyp.hobbi.swyphobbiback.email.repository.EmailVerificationRepository;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.domain.UserHobbyTag;
import swyp.hobbi.swyphobbiback.user.dto.LoginRequest;
import swyp.hobbi.swyphobbiback.user.dto.LoginResponse;
import swyp.hobbi.swyphobbiback.user.dto.UserCreateRequest;
import swyp.hobbi.swyphobbiback.user.dto.UserResponse;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;
import swyp.hobbi.swyphobbiback.token.service.TokenService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final TokenService tokenService;

    public UserResponse signUp(UserCreateRequest request) {

        if (!emailVerificationRepository.findByEmail(request.getEmail()).map(EmailVerification::isVerified).orElse(false)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword()); //비밀번호 암호화

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .birthYear(request.getBirthYear())
                .birthMonth(request.getBirthMonth())
                .birthDay(request.getBirthDay())
                .gender(request.getGender())
                .mbti(request.getMbti())
                .role("User")
                .userImageUrl(request.getUserImageUrl() != null ? request.getUserImageUrl() : "default.png") //기본값 설정 필요
                .build();

        List<UserHobbyTag> userHobbyTags = request.getHobbyTags().stream()
                .map(hobbyTag -> new UserHobbyTag(user, hobbyTag))
                .toList();
        user.getUserHobbyTags().addAll(userHobbyTags);

        userRepository.save(user); // 유저 정보 저장

        TokenPair tokens = tokenService.generateAndSaveTokens(user.getEmail()); // 자동로그인 위한 토큰 생성 및 저장

        return UserResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .userId(user.getUserId())
                .build();

    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        TokenPair tokens = tokenService.generateAndSaveTokens(user.getEmail()); // 토큰 생성 및 저장

        return LoginResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .userId(user.getUserId())
                .build();

    }
}
