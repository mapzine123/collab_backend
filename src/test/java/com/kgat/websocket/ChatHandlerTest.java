package com.kgat.websocket;

import com.kgat.exception.InvalidTokenException;
import com.kgat.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/*
    @Mock
    - 가짜(Mock) 객체 생성
    - 보통 테스트 대상이 의존하는 객체들을 Mock으로 만들 때 사용
    - '이건 가짜야. 원하는 대로 동작하게 만들 수 있어'

    @InjectMocks
    - Mock 객체들을 주입받을 실제 객체 생성
    - 테스트 대상이 되는 클래스에 사용
    - 이건 진짜야, 근대 가짜 객체들을 주입받아서 사용할거야.

    stubbing
    - 테스트에서 실제 객체 대신 가짜 객체를 사용할 때,
      그 가짜 객체가 어떻게 동작해야 하는지 정의하는 것

 */

@ExtendWith(MockitoExtension.class)
class ChatHandlerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ChatHandler chatHandler;

    // 테스트에서 자주 사용하는 값들을 상수로 정의
    private static final String ROOM_ID = "uuid";
    private static final String SENDER_TOKEN = "validTokenForSender";
    private static final String RECEIVER_TOKEN = "validTokenForReceiver";

    // 자주 사용되는 mock 설정을 메서드로 분리
    private WebSocketSession createMockSession(String sessionId, String token) throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn(sessionId);
        when(session.getUri()).thenReturn(new URI("/chat/" + ROOM_ID + "?token=" + token));
        when(session.isOpen()).thenReturn(true);

        return session;
    }

    @Test
    @DisplayName("웹 소켓이 연결되면 세션이 저장되고 채팅방에 매핑된다.")
    void connectionTest() throws Exception {
        // given
        WebSocketSession session = mock(WebSocketSession.class);

        // mock 설정
        when(session.getId()).thenReturn("session1");

        // 연결 요청 URI에 방 ID 포함
        URI uri = new URI("/chat/uuid/?token=validToken");
        when(session.getUri()).thenReturn(uri);

        when(jwtTokenProvider.validateToken("validToken")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("validToken")).thenReturn("user1");
        ChatHandler chatHandler = new ChatHandler(jwtTokenProvider);
        String roomId = chatHandler.getRoomId(uri);

        // when
        chatHandler.afterConnectionEstablished(session);
        // then
        // 1. 전체 세션에 저장되었는지 확인
        assertThat(chatHandler.getSession("session1")).isEqualTo(session);
        // 2. 채팅방 세션에 저장되었는지 확인
        assertThat(chatHandler.getRoomSessions(roomId)).contains(session);
    }
    
    @Test
    @DisplayName("웹 소켓 연결 해제 시 세션이 제거된다")
    void disconnectTest() throws Exception {
        // given
        // session 연결, 채팅방 매핑
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        URI uri = new URI("/chat/uuid/?token=validToken");
        when(session.getUri()).thenReturn(uri);
        
        // 세션 연결
        ChatHandler chatHandler = new ChatHandler(jwtTokenProvider);
        when(jwtTokenProvider.validateToken("validToken")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("validToken")).thenReturn("user1");

        chatHandler.afterConnectionEstablished(session);

        String roomId = chatHandler.getRoomId(uri);

        // when
        chatHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // then
        assertThat(chatHandler.getSession("session1")).isNull();
        assertThat(chatHandler.getRoomSessions(roomId)).doesNotContain(session);
    }

    @Test
    @DisplayName("채팅방에 메시지를 보내면 같은 채팅방의 모든 세션이 메시지를 받는다")
    void sendMessageTest() throws Exception {
        //given
        // 채팅방 "uuid"에 있는 두 개의 세션 설정
        WebSocketSession sender = createMockSession("session1", SENDER_TOKEN);
        WebSocketSession receiver = createMockSession("session2", RECEIVER_TOKEN);
        
        // JWT 설정
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(SENDER_TOKEN)).thenReturn("sender");
        when(jwtTokenProvider.getUserIdFromToken(RECEIVER_TOKEN)).thenReturn("receiver");

        // 세션 연결
        chatHandler.afterConnectionEstablished(sender);
        chatHandler.afterConnectionEstablished(receiver);

        // when
        chatHandler.handleTextMessage(sender, new TextMessage("안녕하세요"));

        // then
        /*
            verify(sender)
            - sender(mock객체)의 메서드 호출을 검증
            - "이 mock 객체의 특정 메서드가 호출되었는지 확인하겠다"는 의미
            
            sendMessage()
            - 검증하고자 하는 메서드
            
            any(TextMessage.class)
            - 파라미터의 구체적인 값은 상관없이, TextMessage 타입이기만 하면 됨
            
         */
        verify(sender).sendMessage(any(TextMessage.class));
        verify(receiver).sendMessage(any(TextMessage.class));

    }

    @Test
    @DisplayName("JWT 토큰이 유효하지 않으면 웹소켓 연결이 거부된다")
    void connectWithInvalidTokenTest() throws Exception {
        // given
        WebSocketSession session = mock(WebSocketSession.class);

        // 잘못된 토큰이 포함됨
        URI uri = new URI("/chat/uuid/token=invalid_token");
        when(session.getUri()).thenReturn(uri);

        ChatHandler chatHandler = new ChatHandler(jwtTokenProvider);

        // when & then
        assertThrows(InvalidTokenException.class, () -> {
            chatHandler.afterConnectionEstablished(session);
        });
    }
}