package com.kgat.controller;

import com.kgat.entity.ChatRoomUser;
import com.kgat.entity.User;
import com.kgat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<User>> getChatRoomUsers(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("chatRoomId") String chatRoomId) {
        System.out.println(userDetails.getUsername());
        List<User> members = chatService.getChatRoomUsers(chatRoomId);
        return ResponseEntity.ok(members);
    }
}
