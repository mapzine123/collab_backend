package com.kgat.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatRoomDTO {
    private String name;
    private List<String> userIds;
}
