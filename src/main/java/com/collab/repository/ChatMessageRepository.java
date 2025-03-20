package com.collab.repository;

import com.collab.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom_IdOrderByCreateAtDesc(String roomId);

    Page<ChatMessage> findByChatRoom_IdOrderByCreateAtDesc(String roomId, Pageable pageable);
}
