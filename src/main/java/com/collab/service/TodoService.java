package com.collab.service;

import com.collab.dto.TodoChangesDTO;
import com.collab.dto.TodoChangeRequestDTO;
import com.collab.entity.Todo;
import com.collab.entity.User;
import com.collab.repository.TodoRepository;
import com.collab.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public List<TodoChangeRequestDTO> findAllTodos() {
        List<Todo> todos = (List<Todo>) todoRepository.findAll();
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TodoChangeRequestDTO> findAllTodos(String userId) {
        List<Todo> todos = todoRepository.findAllByUser(userId);
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void upsertTodos(List<TodoChangeRequestDTO> todoChangeRequestDTOS, User user) {
        List<Todo> todos = todoChangeRequestDTOS.stream()
                .map(dto -> convertToEntity(dto, user))
                .collect(Collectors.toList());
        todoRepository.saveAll(todos);
    }

    private void deleteTodos(List<TodoChangeRequestDTO> todoChangeRequestDTOS, User user) {
        List<Todo> todos = todoChangeRequestDTOS.stream()
                .map(dto -> convertToEntity(dto, user))
                .collect(Collectors.toList());
        todoRepository.deleteAll(todos);
    }

    public TodoChangeRequestDTO addTodo(TodoChangeRequestDTO todoChangeRequestDTO, String userId) {
        User user = userRepository.findById(userId).get();
        Todo todo = convertToEntity(todoChangeRequestDTO, user);
        return convertToDTO(todoRepository.save(todo));
    }

    // 배치 단위로 온 Todo 처리
    public void processBatchUpdate(TodoChangesDTO changes, String userId) {
        User user = new User(userId);

        if(changes.getUpdated() != null) {
            upsertTodos(changes.getUpdated(), user);
        }

        if(changes.getDeleted() != null) {
            deleteTodos(changes.getDeleted(), user);
        }
    }

    // Entity -> DTO
    private TodoChangeRequestDTO convertToDTO(Todo todo) {
        return TodoChangeRequestDTO.builder()
                .id(todo.getId())
                .content(todo.getContent())
                .department((todo.getUser().getDepartment()))
                .name(todo.getUser().getName())
                .completed(todo.isCompleted())
                .userId(todo.getUser().getId())
                .created(todo.getCreated())
                .updated(todo.getUpdated())
                .build();
    }

    // DTO -> Entity
    private Todo convertToEntity(TodoChangeRequestDTO dto, User user) {
        return Todo.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .completed(dto.isCompleted())
                .user(user)
                .build();
    }
}
