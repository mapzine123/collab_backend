package com.kgat.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgat.dto.ChatMessage;
import com.kgat.entity.ChatRoom;
import com.kgat.entity.User;
import com.kgat.security.JwtTokenProvider;
import com.kgat.service.ChatRoomService;
import com.kgat.service.ChatService;
import com.kgat.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final UserService userService;

    // 세션 저장소
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> sessionRoomMap = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> sessionUserMap = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> sessionTokenMap = new ConcurrentHashMap<>();
    private final ChatRoomService chatRoomService;
    private final JwtTokenProvider jwtTokenProvider;


    // 웹 소켓 연결이 처음 수립될 때 호출되는 메서드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // URL에서 토큰 파라미터 추출
        String token = extractTokenFromSession(session);
        sessionTokenMap.put(session, token); // 토큰 저장

        String userId = jwtTokenProvider.getUserIdFromToken(token);
        sessionUserMap.put(session, userId);
    }

    private String extractTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if(query != null && query.startsWith("token=")) {
            return query.substring(6); // token= 제거
        }
        return null;
    }

    // 클라이언트로부터 메시지를 받았을 때 호출되는 메서드
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("반은 메시지: {}", payload);
        try {
            // JSON 문자열을 ChatMessage 객체로 변환
            ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);

            // 채팅방 ID로 해당 채팅방을 조회
            ChatRoom room = chatRoomService.findRoomById(chatMessage.getRoomId());

            if (room != null) {
                handleChatMessage(session, chatMessage, room);
            } else {
                // 채팅방이 존재하지 않으면 에러메시지 전송
                ChatMessage errorMessage = new ChatMessage();
                errorMessage.setType(ChatMessage.MessageType.ERROR);
                errorMessage.setContent("존재하지 않는 채팅방입니다.");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMessage)));
            }
        } catch(IOException e) {
            log.error("메시지 처리 중 오류 발생", e);
            ChatMessage errorMessage = new ChatMessage();
            errorMessage.setType(ChatMessage.MessageType.ERROR);
            errorMessage.setContent("메시지 처리 중 오류가 발생했습니다.");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMessage)));
        }

    }

    // 메시지 타입에 따라 적절한 처리 메서드 호출
    private void handleChatMessage(WebSocketSession session, ChatMessage chatMessage, ChatRoom room) throws IOException {
        switch(chatMessage.getType()) {
            case ENTER :
                handleEnterMessage(session, chatMessage, room);
                break;
            case TALK :
                handleTalkMessage(session, chatMessage, room);
                break;
            case LEAVE :
                handleLeaveMessage(session, chatMessage, room);
                break;
            case ERROR :
                handleErrorMessage(session, chatMessage);
                break;
            default :
                handleUnknownMessageType(session, chatMessage);
                break;
        }
    }

    // 사용자가 채팅방에 입장할 때의 처리
    private void handleEnterMessage(WebSocketSession session, ChatMessage message, ChatRoom room) throws IOException {
        // JWT 토큰엣어 사용자 ID 추출
        String token = message.getSender();
        String userId = jwtTokenProvider.getUserIdFromToken(token);

        // 세션 정보 저장
        roomSessions.computeIfAbsent(room.getId(), k -> new CopyOnWriteArraySet<>()).add(session);
        sessionRoomMap.put(session, room.getId());
        sessionUserMap.put(session, message.getSender());

        // 사용자 정보 조회
        User user = userService.findById(userId);
        message.setSenderName(user.getName());
        message.setSenderDepartment(user.getDepartment());

        // 입장 메시지 전송
        message.setContent(String.format("%s (%s)님이 입장하셨습니다.", user.getName(), user.getDepartment()));
        sendMessageToRoom(room.getId(), message);
    }

    // 일반 대화 메시지 처리
    private void handleTalkMessage(WebSocketSession session, ChatMessage message, ChatRoom room) throws IOException {
        try {
            // 사용자 정보 조회
            String token = sessionTokenMap.get(session);
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            User user = userService.findById(userId);

            message.setSenderName(user.getName());
            message.setSenderDepartment(user.getDepartment());
            message.setSender(token);

            sendMessageToRoom(room.getId(), message);
        } catch(Exception e) {
            log.error("메시지 전송 중 오류 발생", e);
        }

    }

    // 사용자가 채팅방을 나갈 때의 처리
    private void handleLeaveMessage(WebSocketSession session, ChatMessage message, ChatRoom room) throws IOException {
        // JWT 토큰엣어 사용자 ID 추출
        String token = message.getSender();
        String userId = jwtTokenProvider.getUserIdFromToken(token);

        // 세션 정보 제거
        Set<WebSocketSession> sessions = roomSessions.get(room.getId());
        if(sessions != null) {
            sessions.remove(session);
        }
        sessionRoomMap.remove(session);
        sessionUserMap.remove(session);

        // 사용자 정보 조회
        User user = userService.findById(userId);
        message.setSenderName(user.getName());
        message.setSenderDepartment(user.getDepartment());

        // 퇴장 메시지 전송
        message.setContent(String.format("%s (%s)님이 퇴장하셨습니다.", user.getName(), user.getDepartment()));
        sendMessageToRoom(room.getId(), message);
    }

    // 에러 메시지 처리
    private void handleErrorMessage(WebSocketSession session, ChatMessage message) throws IOException {
        // 에러 메시지는 해당 세션에만 전송
        log.error("채팅 에러 발생 : {}", message.getContent());
        TextMessage errorMessage = new TextMessage(objectMapper.writeValueAsString(message));
        session.sendMessage(errorMessage);
    }

    // 알 수 없는 메시지 타입을 처리하는 메서드
    private void handleUnknownMessageType(WebSocketSession session, ChatMessage message) throws IOException {
        log.warn("알 수 없는 메시지 타입 : {}", message.getType());
        ChatMessage errorMessage = new ChatMessage();
        errorMessage.setType(ChatMessage.MessageType.ERROR);
        errorMessage.setContent("알 수 없는 메시지 타입입니다.");

        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(message));
        session.sendMessage(textMessage);
    }

    // 특정 채팅방의 모든 접속자에게 메시지를 전송
    private void sendMessageToRoom(String roomId, ChatMessage message) throws IOException {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if(sessions != null) {
            TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(message));
            for(WebSocketSession session : sessions) {
                if(session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }
        }
    }

    // 웹 소켓 연결이 종료될 때 호출되는 메서드
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug("WebSocket 연결 시도 - Session ID: {}", session.getId());

        String userId = sessionUserMap.remove(session);
        log.info("WebSocket 연결 종료 : {} - {}", session.getId(), userId);
    }
}
