package com.kgat.dto;

import com.kgat.entity.Todo;
import lombok.Data;

import java.util.List;

@Data
public class TodoChangesDTO {
    private List<TodoDTO> updated;
    private List<TodoDTO> deleted;
}
