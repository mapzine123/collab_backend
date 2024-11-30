package com.kgat.service;

import com.kgat.entity.ChatMessage;
import com.kgat.entity.ChatRoom;
import com.kgat.entity.ChatRoomUser;
import com.kgat.entity.User;
import com.kgat.exception.ChatRoomNotFoundException;
import com.kgat.repository.ChatMessageRepository;
import com.kgat.repository.ChatRoomRepository;
import com.kgat.repository.ChatRoomUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomUserRepository chatRoomUserRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatService chatService;

    /*
    * 채팅방 생성 테스트
    * 시나리오 :
    * 1. 사용자1이 사용자2를 초대
    * 2. 새로운 채팅방 생성
    * 3. 두 사용자가 채팅방 멤버로 등록됨
    */


    @Test
    @DisplayName("채팅방에 초대를 하면 채팅방이 생성되고 두 사용자가 참여자로 등록된다.")
    void createChatRoomByInvitationTest() {
        // given : 초대하는 사용자와 초대받는 사용자 설정
        User inviter = new User("user1");
        User invitee = new User("user2");

        // when : 채팅방 초대 요청
        ChatRoom chatRoom = chatService.intiveToChat(inviter, invitee);

        // then : 채팅방이 정상적으로 생성되었는지 검증
        assertNotNull(chatRoom, "채팅방이 생성되어야 함");
        assertNotNull(chatRoom.getRoomId(), "채팅방 id가 생성되어야함");

        // then : 채팅방에 두 사용자가 정상적으로 등록되었는지 검증
        assertEquals(2, chatRoom.getUsers().size(), "채팅방에 2명의 사용자가 있어야 함");

        List<ChatRoomUser> users = chatRoom.getUsers();
        assertTrue(users.stream().anyMatch(u -> u.getUser().equals(inviter)), "초대한 사용자가 채팅방 멤버로 존재해야함");
        assertTrue(users.stream().anyMatch(u -> u.getUser().equals(invitee)), "초대받은 사용자가 채팅방 멤버로 존재해야함");
    }

    @Test
    @DisplayName("채팅방에서 메세지를 전송할 수 있다.")
    void sendMessageToChatRoomTest() {
        // given
        User sender = new User("user1");
        ChatRoom chatRoom = ChatRoom.create();
        String content = "안녕하세요";

        // mock 설정
        /*
        * Mockito 프레임워크를 사용한 Test Double 설정
        * when() : ~할 때 라는 상황을 설정
        * chatRoomRepository.findById(any())
        *   chatRoomRepository의 findById 메서드가 호출될 때
        *   any() : 어떤 인자가 들어와도 적용된다는 의미
        * thenReturn(Optional.of(chatRoom))
        *   그러면 Optional.of(chatRoom)을 반환
        *   실제 DB 조회 대신 미리 준비한 chatRoom객체를 Optional로 감싸서 반환
        * 
        * 장점
        * - DB에 의존하지 않아 테스트의 독립성 보장
        * - 실제 DB 조회보다 빨라 테스트 속도 향상
        * - DB 에러 상황 등 특정 상황 테스트 용이
        */
        when(chatRoomRepository.findById(any()))
                .thenReturn(Optional.of(chatRoom));

        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ChatMessage message = chatService.sendMessage(chatRoom.getId(), sender, content);

        // then : 메시지가 정상적으로 저장되었는지 검증
        assertNotNull(message, "메시지가 생성되어야함");
        assertEquals(content, message.getContent(), "메시지 내용이 일치해야함");
        assertEquals(sender, message.getSender(), "발신자가 일치해야함");
        assertEquals(chatRoom, message.getChatRoom(), "채팅방이 일치해야함");
        assertNotNull(message.getSentAt(), "전송 시간이 설정되어야함");
    }

    @Test
    @DisplayName("존재하지 않는 채팅방에는 메시지를 보낼 수 없다.")
    void cannotSendmessageToNonExistentRoomTest() {
        // given : 존재하지 않는 채팅방 Id, 사용자, 메시지
        User sender = new User("user1");
        Long nonExistentRoomId = 999L;
        String content = "테스트 메시지";

        // mock 설정
        when(chatRoomRepository.findById(nonExistentRoomId))
                .thenReturn(Optional.empty());

        // when & then : 존재하지 않는 채팅방에 메시지 전송 시도 시 예외 발생
        assertThrows(ChatRoomNotFoundException.class, () -> {
            chatService.sendMessage(nonExistentRoomId, sender, content);
        });
    }

    @Test
    @DisplayName("채팅방 참여자만 메시지를 보낼 수 있다.")
    void onlyParticipantsCanSendMessageTest() {
        // given : 채팅방과 미참여 사용자 준비
        User sender = new User("user3");
        ChatRoom chatRoom = ChatRoom.create();
        String content = "테스트 메시지";

        // mock 설정
        when(chatRoomRepository.findById(any()))
                .thenReturn(Optional.of(chatRoom));

        // when & then : 미참여자가 메시지 전송 시 예외 발생
        assertThrows(NotChatRoomParticipantException.class, () -> {
            chatService.sendMessage(chatRoom.getId(), sender, content);
        });
    }
}
