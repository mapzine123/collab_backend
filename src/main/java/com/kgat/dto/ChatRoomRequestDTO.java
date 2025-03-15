package com.kgat.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatRoomRequestDTO {
    private String chatRoomName;
    private List<String> users;
}
