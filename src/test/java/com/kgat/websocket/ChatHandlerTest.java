package com.kgat.websocket;

import com.kgat.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/*
    @Mock
    - 가짜(Mock) 객체 생성
    - 보통 테스트 대상이 의존하는 객체들을 Mock으로 만들 때 사용
    - '이건 가짜야. 원하는 대로 동작하게 만들 수 있어'

    @InjectMocks
    - Mock 객체들을 주입받을 실제 객체 생성
    - 테스트 대상이 되는 클래스에 사용
    - 이건 진짜야, 근대 가짜 객체들을 주입받아서 사용할거야.

 */

@ExtendWith(MockitoExtension.class)
class ChatHandlerTest {
    @Mock
    private WebSocketSession webSocketSession;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatHandler chatHandler;

    @Test
    @DisplayName("웹 소켓으로 메시지를 받으면 채팅방의 모든 참여자에게 메시지가 전송된다.")
    void handleChatMessageTest() throws Exception {
        // given : 채팅 JSON
        String messageJson = """
            {
                "type": "CHAT",
                "roomID": "room1",
                "senderID": "user1",
                "content": "안녕하세요"    
            }
            """;
        TextMessage textMessage = new TextMessage(messageJson);

        // when
        chatHandler.handleTextMessage(webSocketSession, textMessage);

        // then
        verify(webSocketSession, times(1)).sendMessage(any(TextMessage.class))
    }
}