package swyp.hobbi.swyphobbiback.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter { //Access Token 인증 필터

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal( //Access Token 검사
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = jwtTokenProvider.getAccessTokenFromCookies(request); //HTTP 요청 헤더에서 Access Token 뽑아오기

        if (accessToken != null) {//액세스 토큰 존재시
            try {
                jwtTokenProvider.validateToken(accessToken); //토큰 유효성 검사
                String email = jwtTokenProvider.getEmailFromToken(accessToken);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                log.info("Authority : {}", userDetails.getAuthorities().toString());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication); //사용자 인증 정보 등록

            } catch (CustomException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\":401,\"code\":\"" + e.getErrorCode().getCode() + "\",\"message\":\"" + e.getMessage() + "\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
