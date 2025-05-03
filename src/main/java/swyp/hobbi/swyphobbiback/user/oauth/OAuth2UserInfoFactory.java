package swyp.hobbi.swyphobbiback.user.oauth;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        switch (provider.toLowerCase()) { //provider에 따라 클래스 반환
            case "google":
                return new GoogleUserInfo(attributes);
            case "kakao":
                return new KakaoUserInfo(attributes);
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }
}
