package swyp.hobbi.swyphobbiback.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry corsRegistry) {
                corsRegistry.addMapping("/**") //모든 엔드포인트에 CORS 적용
                        .allowedOrigins("http://localhost:3000", "https://swyp-hobbi-front.vercel.app", "https://hobbi.co.kr")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true) //쿠키와 Authorization 헤더 허용
                        .maxAge(3600); //Preflight 결과 캐싱 1시간
            }
        };
    }
}
