package com.kgat.service;

import com.kgat.entity.ChatMessage;
import com.kgat.entity.ChatRoom;
import com.kgat.entity.ChatRoomUser;
import com.kgat.entity.User;
import com.kgat.exception.ChatRoomNotFoundException;
import com.kgat.exception.NotChatRoomParticipantException;
import com.kgat.repository.ChatMessageRepository;
import com.kgat.repository.ChatRoomRepository;
import com.kgat.repository.ChatRoomUserRepository;
import com.kgat.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final UserRepository userRepository;


    public ChatRoom intiveToChat(User inviter, User invitee) {
        // 유효성 검증
        if(inviter.equals(invitee)) {
            throw new IllegalArgumentException(("자기 자신을 초대할 수 없습니다."));
        }

        // 채팅방 생성 및 사용자 추가
        ChatRoom chatRoom = ChatRoom.create();

        ChatRoomUser inviterUser = ChatRoomUser.create(chatRoom, inviter);
        ChatRoomUser inviteeUser = ChatRoomUser.create(chatRoom, invitee);

        chatRoom.addUser(inviterUser);
        chatRoom.addUser(inviteeUser);

        return chatRoom;
    }

    @Transactional
    public ChatMessage sendMessage(Long roomId, String senderId, String content) {
        // 유저 조회
        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatRoomNotFoundException("채팅방을 찾을 수 없습니다."));

        // 채팅방 참여자인지 검증
        boolean isParticipant = chatRoom.getUsers().stream()
                .map(ChatRoomUser::getUser)
                .anyMatch(user -> user.equals(sender));

        if(!isParticipant) {
            throw new NotChatRoomParticipantException("NotParticipants");
        }

        // 메세지 생성
        ChatMessage message = ChatMessage.create(chatRoom, sender, content);

        // 메시지 저장 및 반환
        return chatMessageRepository.save(message);
    }
}
