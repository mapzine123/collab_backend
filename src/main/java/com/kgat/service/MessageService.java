package com.kgat.service;

import com.kgat.dto.ChatMessageDTO;
import com.kgat.entity.ChatMessage;
import com.kgat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessageDTO> getMessages(String roomId, String username) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom_IdOrderByCreateAtDesc(roomId);

        return messages.stream()
                .map(message -> ChatMessageDTO.builder()
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
