package com.kgat.service;

import com.kgat.entity.ChatRoom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomUserRepository chatRoomUserRepository;

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
        String inviter = "user1";
        String invitee = "user2";

        // when : 채팅방 초대 요청
        ChatRoom chatRoom = chatService.intiveToChat(inviter, invitee);

        // then : 채팅방이 정상적으로 생성되었는지 검증
        assertNotNull(chatRoom, "채팅방이 생성되어야 함");
        assertNotNull(chatRoom.getRoomId(), "채팅방 id가 생성되어야함");

        // then : 채팅방에 두 사용자가 정상적으로 등록되었는지 검증
        assertEquals(2, chatRoom.getUsers().size(), "채팅방에 2명의 사용자가 있어야 함");

        List<ChatRoomUser> users = chatRoom.getUsers();
        assertTrue(users.stream().anyMatch(u -> u.getUserId().equals(inviter)), "초대한 사용자가 채팅방 멤버로 존재해야함");
        assertTrue(users.stream().anyMatch(u -> u.getUserId().equals(invitee)), "초대받은 사용자가 채팅방 멤버로 존재해야함");
    }
}