package com.kgat.dto;

import com.kgat.entity.Todo;
import lombok.Data;

import java.util.List;

@Data
public class TodoChangesDTO {
    private List<TodoDTO> added;
    private List<TodoDTO> deleted;
    private List<TodoDTO> modified;
}
