package swyp.hobbi.swyphobbiback.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import swyp.hobbi.swyphobbiback.common.security.JwtAuthenticationFilter;
import swyp.hobbi.swyphobbiback.common.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    //비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {}) // CORS 따로 관리
                .csrf(csrf -> csrf.disable()) // CSRF 끄기 (토큰이므로)
                .formLogin(login -> login.disable()) // form 로그인 페이지 제거
                .httpBasic(basic -> basic.disable()) // Basic 인증 제거
                .authorizeHttpRequests(auth -> auth
                        //인증 없이 허용할 경로 설정
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/user/**",
                                "/api/v1/email/**",
                                "/api/v1/token/**"
                        ).permitAll()
                        .anyRequest().authenticated() // 나머지는 인증 필요

                ).addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}
