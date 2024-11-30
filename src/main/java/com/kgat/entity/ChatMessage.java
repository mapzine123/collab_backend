package com.kgat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    private String content;
    private LocalDateTime sentAt;

    // 생성 메서드
    public static ChatMessage create(ChatRoom chatRoom, User sender, String content) {
        ChatMessage message = new ChatMessage();
        message.chatRoom = chatRoom;
        message.sender = sender;
        message.content = content;
        message.sentAt = LocalDateTime.now();
        return message;
    }
}
