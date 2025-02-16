package com.kgat.config;

import com.kgat.security.JwtAuthenticationFilter;
import com.kgat.security.JwtTokenProvider;
import com.kgat.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Cors 설정 추가
                // 다른 도메인에서 API 요청이 가능하도록 허용할지 결정하는 정책
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Cross-Site Request Forgery 공격 방어 기능 비활성화
                // JWT 토큰 사용시 CSRF 방어가 필요없음
                // 주로 세션 쿠키 기반의 인증 시스템에서 필요
                .csrf(csrf -> csrf.disable())

                // HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll() // 로그인
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll() // 회원가입
                        .requestMatchers(HttpMethod.GET, "/api/articles").permitAll() // 인증 필요없는 GET 매핑
                        .requestMatchers("/ws/chat/**").permitAll()
                        .anyRequest().authenticated() // 그 외 api는 인증 필요
                )
                // JWT 인증 필터 추가
                // JwtAuthenticationFilter : JWT 토큰을 검증하여 사용자를 인증하는 역할
                // UsernamePasswordAuthenticationFilter : Spring Security 기본 로그인 필터
                // 기본 로그인 필터 앞에 JWT 토큰 검증 필터 붙히기
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)

                // 로그아웃 설정 정의
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout") // /api/users/logout URL로 로그아웃 요청 처리
                        .invalidateHttpSession(true) // 로그아웃시 세션 무효화, 세션 정보 제거
                        .permitAll() // 로그아웃 요청은 인증 없이 누구나 접근 가능
                )

                // 예외 처리 설정
                .exceptionHandling(exceptionHandling -> // 인증 과정에서 발생하는 예외 처리
                        exceptionHandling
                                // 인증이 필요한 요청에서 사용자가 인증되지 않은 경우 이 핸들러 실행
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 응답코드를 401 (Unauthorized로 설정
                                    response.getWriter().write("Unauthorized"); // 응답 본문에 Unauthorized 메시지 작성
                                })
                );


        return http.build(); //
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 허용할 Origin 설정, 이 도메인에서 오는 요청만 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드 설정, OPTION 메서드는 CORS 사전 요청으로 사용
        configuration.setAllowCredentials(true); // 인증 정보를 포함한 요청을 허용할지 여부, JWT 토큰이 쿠키에 저장된 경우 이 설정이 필요
        configuration.setAllowedHeaders(List.of("*")); // 허용할 HTTP 요청 헤더 설정, Content-Type이나 Authorization 같은 인증 관련 헤더 등..

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // CORS 설정을 적용할 URL 패턴 정의

        return source;
    }

    /*
    * 사용자 인증을 담당하는 메서드
    * UserDetailsService : 사용자 정보(아이디, 비밀번호, 권한 등)을 로드하는 서비스
    * PasswordEncoder: 비밀번호를 암호화 / 복호화 하는 인코더
    *
    * AuthenticationManager : Spring Security가 로그인 과정에서 사용자 인증을 할 때 사용
    */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
