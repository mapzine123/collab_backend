package com.kgat.repository;

import com.kgat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
