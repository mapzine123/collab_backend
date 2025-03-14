package com.kgat.service;

import com.kgat.dto.ChatUserRequestDTO;
import com.kgat.entity.ChatRoom;
import com.kgat.entity.ChatRoomUser;
import com.kgat.entity.User;
import com.kgat.repository.ChatRoomRepository;
import com.kgat.repository.ChatRoomUserRepository;
import com.kgat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    public ChatRoom createRoom(String chatRoomName, String creatorId, List<String> userIds) {
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.create(chatRoomName);

        // 생성자를 채팅방에 추가
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Creator not found"));

        addUserToChatRoom(chatRoom, creator);

        // 초대된 사용자들을 채팅방에 추가
        List<User> users = userRepository.findAllById(userIds);
        users.forEach(user -> addUserToChatRoom(chatRoom, user));

        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    private void addUserToChatRoom(ChatRoom chatRoom, User user) {
        ChatRoomUser chatRoomUser = ChatRoomUser.create(chatRoom, user);
        chatRoom.addUser(chatRoomUser);
    }

    public List<ChatRoom> findAllRoomsByUserId(String userId) {
        return chatRoomRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getMyChatRooms(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return chatRoomUserRepository.findByUserAndActiveTrue(user)
                .stream()
                .map(ChatRoomUser::getChatRoom)
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ChatRoom findRoomById(String roomId) {
        // roomId로 채팅방을 조회하고, 없는 경우 예외를 발생시킨다.
        // reedOnly = true를 사용하여 조횟 성능을 최적화함
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
    }

    @Transactional
    public void addUsers(ChatUserRequestDTO data) {
        ChatRoom chatRoom = chatRoomRepository.findById(data.getRoomId()).get();
        for(String name : data.getUsers()) {
            User user = userRepository.findById(name).get();
            chatRoomUserRepository.save(ChatRoomUser.create(chatRoom, user));
        }
    }

    @Transactional
    public void exitUser(String roomId, String userId) {
        chatRoomUserRepository.deleteByChatRoomIdAndUserId(roomId, userId);
    }
}
