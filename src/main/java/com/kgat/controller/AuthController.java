package com.kgat.controller;

import com.kgat.entity.User;
import com.kgat.security.JwtTokenProvider;
import com.kgat.service.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "인증 관련 API", description = "JWT 토큰 발행을 처리하는 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private  final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> Login(@RequestBody User user) {
        try {
            // UserDetailsService를 사용하여 사용자 정보를 직접 로드
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getId());

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword()));
            // SecurityContextHolder에 인증된 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // JWT 토큰 발급
            String token = jwtTokenProvider.generateToken(userDetails);

            // 사용자 정보와 JWT 토큰 반환
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Authentication failied"));
        }
    }
}