package com.kgat.dto;

import lombok.Data;

/*
    @Data가 포함하는 기능
    - @Getter
    - @Setter
    - @ToString : toString()
    - @EqualsAndHashCode : equals And HashCode
    - @RequiredArgsConstructor : 필수 필드에 대한 생성자
 */

@Data
public class ChatMessageDTO {
    private MessageType type;
    private Long roomId;
    private String senderId;
    private String content;

    public enum  MessageType {
        CHAT, // 채팅
        ENTER, // 입장
        LEAVE // 퇴장
    }
}
