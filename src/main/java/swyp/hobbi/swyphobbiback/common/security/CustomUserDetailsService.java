package swyp.hobbi.swyphobbiback.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithHobbyTags(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + email));

        return new CustomUserDetails(user);
    }
}
