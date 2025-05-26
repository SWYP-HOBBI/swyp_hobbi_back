package swyp.hobbi.swyphobbiback.user.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.user.domain.OauthLogin;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.repository.OAuthLoginRepository;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final OAuthLoginRepository oauthLoginRepository;
    private final UserRepository userRepository;

    public void linkSocialAccount(Long userId, HttpSession session) {
        //세션에서 임시 저장 정보 불러오기
        String socialEmail = (String) session.getAttribute("pendingSocialEmail");
        String provider = (String) session.getAttribute("pendingSocialProvider");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (socialEmail == null || provider == null) {
            throw new IllegalArgumentException("연동할 소셜 계정 정보가 없습니다.");
        }

        if (oauthLoginRepository.existsByProviderEmailAndProvider(socialEmail, provider)) {
            throw new IllegalArgumentException("이미 다른 계정과 연동된 소셜 계정입니다.");
        }

        OauthLogin link = OauthLogin.builder() //소셜 계정 연동
                .user(user)
                .provider(provider)
                .providerEmail(socialEmail)
                .build();

        oauthLoginRepository.save(link);

        //세션 삭제
        session.removeAttribute("pendingSocialEmail");
        session.removeAttribute("pendingSocialProvider");
    }

}
