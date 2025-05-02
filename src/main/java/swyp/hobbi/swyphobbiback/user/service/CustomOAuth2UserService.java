package swyp.hobbi.swyphobbiback.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.oauth.OAuth2UserInfo;
import swyp.hobbi.swyphobbiback.user.oauth.OAuth2UserInfoFactory;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService{

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); //어떤 소셜로그인인지 확인 "google", "kakao"
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());
        String email = userInfo.getEmail();

        System.out.println("소셜 로그인 이메일: " + email);

        // 회원가입 완료된 사용자만 소셜 로그인 가능
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error("USER_NOT_REGISTERED", "회원가입되지 않은 사용자입니다.", null)));

        return oAuth2User;

    }
}
