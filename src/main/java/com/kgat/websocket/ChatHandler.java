package com.kgat.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
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


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        String roomId = getRoomId(session.getUri());

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

    private String getRoomId(URI uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }

    // 테스트를 위한 메서드들
    public WebSocketSession getSession(String id) {
        return sessions.get(id);
    }

    public Set<WebSocketSession> getRoomSessions(String roomdId) {
        return roomSessions.get(roomdId);
    }
}
