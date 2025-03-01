package com.kgat.websocket;

import com.kgat.exception.InvalidTokenException;
import com.kgat.security.JwtTokenProvider;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
    ConcurrentHashMap : Thread-safe한 Map 구현체

    HashMap
    - 동시에 여러 스레드가 접근하면 데이터가 깨질 수 있음
    - synchronized 키워드로 직접 동기화 처리 필요

    ConcurrentHashMap
    - 동시에 여러 스레드가 접근해도 안전
    - 내부적으로 동기화 처리가 되어있음
    - lock을 segment 단위로 나눠서 처리 (부분 잠금)
    
    
    WebSocketSession
    - 실제 클라이언트와의 연결을 추상화한 객체
    - 각 클라이언트 연결마다 하나의 WebSocketSession 생성, 이를 통해 클라이언트와 통신
 */

@Component
public class ChatHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // SessionId -> UserId 매핑
    private final Map<String, String> sessionUserMapping = new ConcurrentHashMap<>();

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ChatHandler(final JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 발신자 방 ID 확인
        String roomId = getRoomId(session.getUri());

        // 해당 방의 모든 세션 가져오기
        Set<WebSocketSession> sessions = roomSessions.getOrDefault(roomId, Collections.emptySet());

        // 방의 모든 세션에게 메시지 전송
        for(WebSocketSession ws : sessions) {
            if(ws.isOpen()) {
                ws.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        String roomId = getRoomId(session.getUri());
        String token = extractToken(session.getUri());

        // 유효한 토큰이 아닐 시 예외 발생
        if(!jwtTokenProvider.validateToken(token)) {
            throw new InvalidTokenException("Invalid JWT token");
        }

        // 사용자 ID 저장
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        sessionUserMapping.put(sessionId, userId);

        // 전체 세션에 저장
        sessions.put(sessionId, session);

        // 채팅방 세션에 저장
        /*
            roomSessoins는 Map임
            computeIfAbsent
            - key가 있으면
                해당 value(Set) 반환

            - key가 없으면
                새로운 HashSet 생성
                생성된 HashSet에 key 매핑
                생성된 HashSet 반환
         */
        roomSessions.computeIfAbsent(roomId, k -> new HashSet<>()).add(session);
    }

    // 토큰 추출
    public String extractToken(URI uri) {
        String query = uri.getQuery();

        if(query != null) {
            String[] params = query.split("&");
            for(String param : params) {
                String[] keyValue = param.split("=");
                if(keyValue.length == 2 && "token".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        throw new InvalidTokenException("Invalid token");
    }

    String getRoomId(URI uri) {
        String path = uri.getPath();
        String roomId = path.substring(path.lastIndexOf('/') + 1);

        // 쿼리 파라미터 제거
        int queryIndex = roomId.indexOf('?');

        return queryIndex > -1 ? roomId.substring(0, queryIndex) : roomId;
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getRoomId(session.getUri());

        // 전체 세션에서 제거
        sessions.remove(session.getId());

        // 채팅방 세션에서 제거
        Set<WebSocketSession> roomSession = roomSessions.get(roomId);
        if(roomSession != null) {
            roomSession.remove(session);

            // 방에 아무도 없으면 방 자체를 제거
            if(roomSession.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
    }

    // 테스트를 위한 메서드들
    public WebSocketSession getSession(String id) {
        return sessions.get(id);
    }

    public Set<WebSocketSession> getRoomSessions(String roomdId) {
        /*
            getOrDefault
            - Map 인터페이스의 메서드
            - 첫번째 파라미터 : 찾으려는 key 값
            - 두번째 파라미터 : key가 없을 때 반환할 기본 값
            
            Collections.emptySet()
            - immutable(불변)의 빈 Set을 반환
            - 새로운 Set을 만드는 것 보다 메모리 효율적임
            - 동일한 빈 Set 인스턴스를 재사용
            
            immutable : 한번 생성되면 그 내용을 변경할 수 없는 객체
            - Thread-safe 함 : 여러 스레드가 동시에 접근해도 값이 변하지 않음
            - Side Effect 방지 : 다른 코드에서 값을 변경할 수 없음
            - 캐싱 가능 : 항상 같은 상태이므로 재사용 가능
         */
        return roomSessions.getOrDefault(roomdId, Collections.emptySet());
    }
}
