package com.kgat.websocket;

import com.kgat.exception.InvalidTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    @DisplayName("웹 소켓이 연결되면 세션이 저장되고 채팅방에 매핑된다.")
    void connectionTest() throws Exception {
        // given
        WebSocketSession session = mock(WebSocketSession.class);

        // mock 설정
        when(session.getId()).thenReturn("session1");

        // 연결 요청 URI에 방 ID 포함
        URI uri = new URI("/chat/uuid");
        when(session.getUri()).thenReturn(uri);

        ChatHandler chatHandler = new ChatHandler();

        // when
        chatHandler.afterConnectionEstablished(session);

        // then
        // 1. 전체 세션에 저장되었는지 확인
        assertThat(chatHandler.getSession("session1")).isEqualTo(session);
        // 2. 채팅방 세션에 저장되었는지 확인
        assertThat(chatHandler.getRoomSessions("uuid")).contains(session);
    }
    
    @Test
    @DisplayName("웹 소켓 연결 해제 시 세션이 제거된다")
    void disconnectTest() throws Exception {
        // given
        // session 연결, 채팅방 매핑
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        URI uri = new URI("/chat/uuid");
        when(session.getUri()).thenReturn(uri);
        
        // 세션 연결
        ChatHandler chatHandler = new ChatHandler();
        chatHandler.afterConnectionEstablished(session);

        // when
        chatHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // then
        assertThat(chatHandler.getSession("session1")).isNull();
        assertThat(chatHandler.getRoomSessions("uuid")).doesNotContain(session);
    }

    @Test
    @DisplayName("채팅방에 메시지를 보내면 같은 채팅방의 모든 세션이 메시지를 받는다")
    void sendMessageTest() throws Exception {
        //given
        // 채팅방 "uuid"에 있는 두 개의 세션 설정
        WebSocketSession sender = mock(WebSocketSession.class);
        WebSocketSession receiver = mock(WebSocketSession.class);

        when(sender.getId()).thenReturn("session1");
        when(receiver.getId()).thenReturn("session2");

        // roomId를 uuid로 설정
        URI uri = new URI("/chat/uuid");
        when(sender.getUri()).thenReturn(uri);
        when(receiver.getUri()).thenReturn(uri);

        // 세션이 열려있다고 설정
        when(sender.isOpen()).thenReturn(true);
        when(receiver.isOpen()).thenReturn(true);

        // 수신자, 발신자 세션 등록
        ChatHandler chatHandler = new ChatHandler();
        chatHandler.afterConnectionEstablished(sender);
        chatHandler.afterConnectionEstablished(receiver);

        TextMessage chatMessage = new TextMessage("안녕하세요");

        // when
        chatHandler.handleTextMessage(sender, chatMessage);

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

        ChatHandler chatHandler = new ChatHandler();

        // when & then
        assertThrows(InvalidTokenException.class, () -> {
            chatHandler.afterConnectionEstablished(session);
        });
    }
}