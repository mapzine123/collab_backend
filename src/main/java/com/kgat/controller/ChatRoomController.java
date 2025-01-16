package com.kgat.controller;

import com.kgat.dto.ChatRoomDTO;
import com.kgat.dto.ChatRoomResponse;
import com.kgat.entity.ChatRoom;
import com.kgat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody ChatRoomDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        ChatRoom chatRoom = chatRoomService.createRoom(request.getName(), userDetails.getUsername(), request.getUserIds());

        return ResponseEntity.ok(new ChatRoomResponse(chatRoom.getRoomId()));
    }
}
