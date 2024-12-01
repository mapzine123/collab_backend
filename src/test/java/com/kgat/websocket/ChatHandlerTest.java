package com.kgat.websocket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;
import static org.assertj.core.api.Assertions.assertThat;
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

 */

@ExtendWith(MockitoExtension.class)
class ChatHandlerTest {

    @Test
    @DisplayName("웹 소켓이 연결되면 세션이 저장된다")
    void connectionTest() throws Exception {
        // given
        WebSocketSession session = mock(WebSocketSession.class);
        ChatHandler chatHandler = new ChatHandler();

        // mock 설정
        when(session.getId()).thenReturn("session1");

        // when
        chatHandler.afterConnectionEstablished(session);

        // then
        assertThat(chatHandler.getSession("session1")).isEqualTo(session);

    }
}