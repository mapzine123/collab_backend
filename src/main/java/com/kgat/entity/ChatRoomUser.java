package com.kgat.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class ChatRoomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime lastReadAt; // 마지막으로 읽은 시간
    private LocalDateTime joinedAt; // 채팅방 참여 시간

    private boolean active = true; // 채팅방 참여 여부

    // 생성 메서드
    public static ChatRoomUser create(ChatRoom chatRoom, User user) {
        ChatRoomUser chatRoomUser = new ChatRoomUser();
        chatRoomUser.chatRoom = chatRoom;
        chatRoomUser.user = user;
        chatRoomUser.lastReadAt = LocalDateTime.now();
        chatRoomUser.joinedAt = LocalDateTime.now();
        return chatRoomUser;
    }

}
