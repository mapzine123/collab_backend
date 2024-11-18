package com.kgat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class TodoDTO {
    private Long id;
    private String content;
    private boolean completed;
    private String userId;  // User 엔티티 대신 userId만
    private LocalDateTime created;
    private LocalDateTime updated;
}
