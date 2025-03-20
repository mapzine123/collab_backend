package com.collab.chatting;

import com.collab.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        log.debug("WebSocket handshake 시도");
        log.debug("URI: {}", request.getURI());
        log.debug("Headers: {}", request.getHeaders());
        // URL에서 token 파라미터 추출
        String query = request.getURI().getQuery();

        if(request.getURI().getPath().endsWith("/info")) {
            return true;
        }

        // 먼저 쿼리 파라미터에서 토큰 찾기
        if (query != null && query.startsWith("token=")) {
            String token = query.substring(6); // "token=" 제거
            log.debug("Token from query: {}", token);

            try {
                if(jwtTokenProvider.validateToken(token)) {
                    String username = jwtTokenProvider.getUserIdFromToken(token);
                    attributes.put("username", username);
                    log.debug("Successfully authenticated user: {}", username);
                    return true;
                }
            } catch(Exception e) {
                log.error("Token validation failed", e);
            }
        }


        // 헤더에서 JWT 토큰 추출
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if(authHeaders != null && !authHeaders.isEmpty()) {
            String token = authHeaders.get(0).replace("Bearer ", "");

            try {
                // JWT 검증
                if(jwtTokenProvider.validateToken(token)) {
                    String username = jwtTokenProvider.getUserIdFromToken(token);
                    attributes.put("username", username);
                    log.info("Successfully authenticated user: {}", username);

                    return true;
                }
            } catch(Exception e) {
                log.error("Token validation failed");
                return false;
            }
        }
        log.warn("Authentication failed");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if(exception != null) {
            log.error("Error during WebSocket handshake");
        }
    }
}
