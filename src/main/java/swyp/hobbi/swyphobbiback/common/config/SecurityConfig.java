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
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetailsService;
import swyp.hobbi.swyphobbiback.common.security.JwtAuthenticationFilter;
import swyp.hobbi.swyphobbiback.common.security.JwtTokenProvider;
import swyp.hobbi.swyphobbiback.user.handler.OAuth2FailureHandler;
import swyp.hobbi.swyphobbiback.user.handler.OAuth2SuccessHandler;
import swyp.hobbi.swyphobbiback.user.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

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
                                "/api/v1/token/**",
                                "/api/v1/oauth/session",
                                "/api/v1/post/*", // 단건 조회 허용
                                "/api/v1/comments/**", // 댓글 목록 조회 무한 스크롤
                                "/error/**",
                                "/api/v1/user/login/oauth2/**",
                                "/api/v1/user/login/oauth2/code/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/v1/post/**",
                                "api/v1/comment/**",
                                "api/v1/like/**",
                                "api/v1/unlike/**",
                                "api/v1/sse/**",
                                "api/v1/notifications/**",
                                "api/v1/challenge/**"
                        ).authenticated()
                        .anyRequest().authenticated() // 나머지는 인증 필요

                ).addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/api/v1/user/login/oauth2") // 인가 요청 경로 설정
                        )
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/api/v1/user/login/oauth2/code/*") // 인가 코드 수신 경로 설정
                        )
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                );

        return http.build();

    }

}
