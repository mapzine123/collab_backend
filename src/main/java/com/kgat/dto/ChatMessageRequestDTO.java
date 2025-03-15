package com.kgat.dto;

import lombok.Data;

@Data
public class ChatMessageRequestDTO {
    public enum MessageType {
        ENTER, TALK, LEAVE, ERROR
    }

    private MessageType type;
    private String roomId;
    private String senderId;
    private String senderName;
    private String senderDepartment;
    private String content;
    private String timestamp;
}
