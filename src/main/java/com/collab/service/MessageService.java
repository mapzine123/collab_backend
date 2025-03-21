package com.collab.service;

import com.collab.dto.ChatMessageResponseDTO;
import com.collab.entity.ChatMessage;
import com.collab.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessageResponseDTO> getMessages(String roomId, String username) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom_IdOrderByCreateAtDesc(roomId);

        return messages.stream()
                .map(message -> ChatMessageResponseDTO.builder()
                        .roomId(message.getId())
                        .content(message.getContent())
                        .senderId(message.getSender().getId())
                        .senderName(message.getSender().getName())
                        .senderDepartment(message.getSender().getDepartment())
                        .createAt(message.getCreateAt())
                        .type(message.getMessageType())
                        .build())
                .collect(Collectors.toList());
    }
}
