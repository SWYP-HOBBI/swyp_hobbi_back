package swyp.hobbi.swyphobbiback.common.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import swyp.hobbi.swyphobbiback.user.domain.User;
import org.springframework.security.core.GrantedAuthority;
import swyp.hobbi.swyphobbiback.userhobbytag.domain.UserHobbyTag;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;
    private Map<String, Object> attribute;

    // 일반 로그인 사용자
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // 소셜 로그인 사용자
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attribute = attributes;
    }

    // 사용자 권한 목록, 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())); // 필요하면 role 여기서 꺼내서 반환
    }

    // 사용자 password
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 사용자 로그인 ID(email)
    public String getEmail() { return user.getEmail(); }

    public Long getUserId() {
        return user.getUserId();
    }

    // 사용자 로그인 ID(email)
    public String getEmail() {
        return user.getEmail();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public String getUserImageUrl() {
        return user.getUserImageUrl();
    }

    public List<UserHobbyTag> getUserHobbyTags() {
        return user.getUserHobbyTags();
    }

    public String getGender() {
        return user.getGender();
    }

    public String getMbti() {
        return user.getMbti();
    }

    public Integer getBirthYear() {
        return user.getBirthYear();
    }

    public Integer getBirthMonth() {
        return user.getBirthMonth();
    }

    public Integer getBirthDay() {
        return user.getBirthDay();
    }

    public Boolean getIsTagExist() {
        return user.getIsTagExist();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
