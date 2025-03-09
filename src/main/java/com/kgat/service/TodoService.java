package com.kgat.service;

import com.kgat.dto.TodoChangesDTO;
import com.kgat.dto.TodoDTO;
import com.kgat.entity.Todo;
import com.kgat.entity.User;
import com.kgat.repository.TodoRepository;
import com.kgat.repository.UserRepository;
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

    public List<TodoDTO> findAllTodos() {
        List<Todo> todos = (List<Todo>) todoRepository.findAll();
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TodoDTO> findAllTodos(String userId) {
        List<Todo> todos = todoRepository.findAllByUser(userId);
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void upsertTodos(List<TodoDTO> todoDTOs, User user) {
        List<Todo> todos = todoDTOs.stream()
                .map(dto -> convertToEntity(dto, user))
                .collect(Collectors.toList());
        todoRepository.saveAll(todos);
    }

    private void deleteTodos(List<TodoDTO> todoDTOs, User user) {
        List<Todo> todos = todoDTOs.stream()
                .map(dto -> convertToEntity(dto, user))
                .collect(Collectors.toList());
        todoRepository.deleteAll(todos);
    }

    public TodoDTO addTodo(TodoDTO todoDTO, String userId) {
        User user = userRepository.findById(userId).get();
        Todo todo = convertToEntity(todoDTO, user);
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
    private TodoDTO convertToDTO(Todo todo) {
        return TodoDTO.builder()
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
    private Todo convertToEntity(TodoDTO dto, User user) {
        return Todo.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .completed(dto.isCompleted())
                .user(user)
                .build();
    }
}
