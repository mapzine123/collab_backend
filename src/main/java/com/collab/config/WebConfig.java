package com.collab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Boot의 CROS 설정을 위한 클래스
 * 특정 도메인에서 백엔드 API에 접근할 수 있또록 허용하는 역할을 함
 */
@Configuration
public class WebConfig {
    /**
     * CORS(Cross-Origin Resource Sharing) 설정을 담당하는 Bean 정의
     * 클라이언트에서 백엔드 API에 요청을 보낼 때 CORS 정책을 적용하여 특정 도메인에서만 접근 가능하도록 설정
     *
     * @return WebMvcConfigurer 객체를 반환하여 CORS 설정 적용
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 엔드포인트에 대해 CORS 허용
                        .allowedOrigins("http://localhost:3000")  // 요청 허용할 도메인 혹은 IP
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드 지정
                        .allowedHeaders("*") // 허용할 요청 헤더 지정
                        .allowCredentials(true); // 쿠키 및 인증 정보 포함 허용
            }
        };
    }
}
