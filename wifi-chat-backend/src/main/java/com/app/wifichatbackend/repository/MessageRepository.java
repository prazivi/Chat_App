package com.app.wifichatbackend.repository;


import com.app.wifichatbackend.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Get all messages in a room, oldest first (for displaying chat)
    List<Message> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    // Same but with pagination (for loading older messages)
    // Page = a chunk of results (e.g., 50 messages at a time)
    Page<Message> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);
}
