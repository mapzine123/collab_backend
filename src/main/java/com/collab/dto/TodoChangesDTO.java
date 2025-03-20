package com.collab.dto;

import lombok.Data;

import java.util.List;

@Data
public class TodoChangesDTO {
    private List<TodoChangeRequestDTO> updated;
    private List<TodoChangeRequestDTO> deleted;
}
