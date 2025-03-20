package com.collab.controller;

import com.collab.dto.TodoChangesDTO;
import com.collab.dto.TodoChangeRequestDTO;
import com.collab.entity.Todo;
import com.collab.service.TodoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "업무리스트 관련 API", description = "업무리스트 관련 CRUD 작업을 처리하는 API")
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    // 해당 User의 전체 to-do 반환
    @GetMapping
    public ResponseEntity<List<TodoChangeRequestDTO>> getTodos(@AuthenticationPrincipal UserDetails userDetails) {
        List<TodoChangeRequestDTO> todos = todoService.findAllTodos();

        if(todos.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());  // 빈 배열 반환
        } else {
            return ResponseEntity.ok(todos);
        }
    }

    // To-do 추가
    @PostMapping
    public ResponseEntity<TodoChangeRequestDTO> addTodo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody TodoChangeRequestDTO todoChangeRequestDTO) {
        String userId = userDetails.getUsername();
        TodoChangeRequestDTO savedTodo = todoService.addTodo(todoChangeRequestDTO, userId);
        System.out.println(savedTodo);
        return ResponseEntity.ok(savedTodo);
    }

    // 수정 / 삭제는 배치단위로 일괄처리
    @PostMapping("/batch")
    public ResponseEntity<List<Todo>> batchTodo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody TodoChangesDTO changes) {
        String userId = userDetails.getUsername();
        todoService.processBatchUpdate(changes, userId);
        return ResponseEntity.ok().build();
    }
}
