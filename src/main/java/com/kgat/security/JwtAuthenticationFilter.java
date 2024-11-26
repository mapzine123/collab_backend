package com.kgat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
* JWT 인증 필터
* HTTP 요청마다 한번씩 실행됨
* 요청에 포함된 JWT 토큰을 검증하고 인증 정보를 Spring Security의 컨텍스트에 설정
*/


public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // JWT 토큰의 생성, 검증, 파싱 기능을 담당하는 클래스
    private JwtTokenProvider tokenProvider;
    // 사용자 정도를 Load하는 클래스
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(request);

            if(StringUtils.hasText(token)) {
                String userId = tokenProvider.getUserIdFromToken(token);

                // UserDetails 객체 생성
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                // Authentication 객체 생성
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // SecurityContext에 Authentication 객체 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
            filterChain.doFilter(request, response);

        } catch(Exception e) {
            throw e;
        }

    }

    // 토큰 추출
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // "Bearer " 문자열 뒤의 JWT 토큰 부분만 잘라서 변환
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
