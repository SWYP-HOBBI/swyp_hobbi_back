package swyp.hobbi.swyphobbiback.user.oauth;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;

        return (String) kakaoAccount.get("email");
    }
}
