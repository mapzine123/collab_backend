package com.kgat.controller;

import com.kgat.dto.*;
import com.kgat.entity.ChatRoom;
import com.kgat.service.ChatRoomService;
import com.kgat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.aspectj.bridge.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody ChatRoomDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        ChatRoom chatRoom = chatRoomService.createRoom(request.getName(), userDetails.getUsername(), request.getUserIds());

        return ResponseEntity.ok(new ChatRoomResponse(chatRoom.getId()));
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomResponseDTO>> getChatRooms(@AuthenticationPrincipal UserDetails userDetails) {
        List<ChatRoom> chatRooms = chatRoomService.getMyChatRooms(userDetails.getUsername());

        List<ChatRoomResponseDTO> chatRoomDTOs = chatRooms.stream()
                .map(chatRoom -> ChatRoomResponseDTO.builder()
                        .id(chatRoom.getId())
                        .name(chatRoom.getName())
                        .users(chatRoom.getUsers().stream()
                                .map(chatRoomUser -> UserDTO.builder()
                                        .id(chatRoomUser.getUser().getId())
                                        .name(chatRoomUser.getUser().getName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(chatRoomDTOs);
    }

    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable String roomId, @AuthenticationPrincipal UserDetails userDetails) {
        List<ChatMessageDTO> messages = messageService.getMessages(roomId, userDetails.getUsername());

        return ResponseEntity.ok(messages);
    }
}
