package com.kgat.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatUserRequestDTO {
    private String roomId;
    private List<String> users;
}
