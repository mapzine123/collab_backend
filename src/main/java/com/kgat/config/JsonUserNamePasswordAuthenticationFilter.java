package com.kgat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

/*
* JSON 형식의 로그인 요청을 처리하는 핀터
* application/json 타입으로 전달된 로그인 요청을 파싱하고, 인증을 처리
*/


public class JsonUserNamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public JsonUserNamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager); // AuthenticationManager 설정
    }

    
//    로그인 요청이 들어올 때 호출, 사용자 인증을 시도하는 역할
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // 요청의 Content-type이 'application/json'이 아니라면 기존 인증 방식 사용
        if(!request.getContentType().equals("application/json")) {
            return super.attemptAuthentication(request, response);
        }

        try {
            // JSON 형식의 요청 본문을 읽어 Map으로 변환
            Map<String, String> loginRequest = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            
            // UserId와 password 추출
            String username = loginRequest.get("userId");
            String password = loginRequest.get("password");
            
            // 토큰을 생성하여 사용자 정보 저장 
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

            // AuthenticationManager를 사용자혀 인증 시도 및 결과 반환
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
