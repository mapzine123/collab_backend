package com.kgat.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    @DisplayName("두 사용자로 새로운 채팅방을 생성할 수 있다.")
    void createChatRoomTest() {
        // Given
        String user1 = "User1";
        String user2 = "User2";

        ChatRoom expectedRoom = new ChatRoom();
        expectedRoom.setUser1(user1);
        expectedRoom.setUser2(user2);

        when(chatRoomRepository.findByUser1AndUser2(user1, user2))
                .thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class)))
                .thenReturn(expectedRoom);

        // When
        ChatRoom newRoom = chatService.createChatRoom(user1, user2);

        // Then
        assertNotNull(newRoom);
        assertEquals(user1, newRoom.getUser1());
        assertEquals(user2, newRoom.getUser2());
        assertNotNull(newRoom.getRoomId());
    }
}