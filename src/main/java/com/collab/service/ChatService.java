package com.collab.service;

import com.collab.entity.ChatMessage;
import com.collab.entity.ChatRoom;
import com.collab.entity.ChatRoomUser;
import com.collab.entity.User;
import com.collab.exception.ChatRoomNotFoundException;
import com.collab.exception.NotChatRoomParticipantException;
import com.collab.repository.ChatMessageRepository;
import com.collab.repository.ChatRoomRepository;
import com.collab.repository.ChatRoomUserRepository;
import com.collab.repository.UserRepository;
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


    public ChatRoom intiveToChat(User inviter, User[] invitees, String channelName) {
        // 채팅방 생성 및 사용자 추가


        return null;
    }

    @Transactional
    public ChatMessage sendMessage(String chatRoomId, String senderId, String content) {
        // 유저 조회
        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ChatRoomNotFoundException("채팅방을 찾을 수 없습니다."));

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

    public List<User> getChatRoomUsers(String chatRoomId) {
        return chatRoomUserRepository.findByChatRoomId(chatRoomId);
    }
}
