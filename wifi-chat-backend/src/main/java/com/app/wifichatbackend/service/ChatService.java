package com.app.wifichatbackend.service;


import com.app.wifichatbackend.dto.ChatMessageDTO;
import com.app.wifichatbackend.model.Message;
import com.app.wifichatbackend.model.User;
import com.app.wifichatbackend.repository.MessageRepository;
import com.app.wifichatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j           // Lombok: creates a logger → log.info("message")
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // Save a message to the database and return the saved version
    @Transactional      // All database operations in this method succeed or all fail
    public ChatMessageDTO processMessage(ChatMessageDTO dto) {

        // Find the sender in the database
        User sender = userRepository.findByUsername(dto.getSenderUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getSenderUsername()));

        // Create the message entity
        Message message = Message.builder()
                .senderId(sender.getId())
                .chatRoomId(dto.getChatRoomId())
                .content(dto.getContent())
                .messageType(dto.getMessageType() != null ? dto.getMessageType() : "TEXT")
                .build();

        // Save to database
        Message saved = messageRepository.save(message);
        log.info("Message saved: id={}, from={}, room={}", saved.getId(), dto.getSenderUsername(), dto.getChatRoomId());

        // Return as DTO (what we send back to clients)
        return ChatMessageDTO.builder()
                .id(saved.getId())
                .senderUsername(dto.getSenderUsername())
                .chatRoomId(saved.getChatRoomId())
                .content(saved.getContent())
                .messageType(saved.getMessageType())
                .status(saved.getStatus())
                .timestamp(saved.getCreatedAt())
                .build();
    }

    public void setUserOnline(String username) {
        userRepository.updateStatus(username, User.UserStatus.ONLINE);
        log.info("User online: {}", username);
    }

    public void setUserOffline(String username) {
        userRepository.updateStatus(username, User.UserStatus.OFFLINE);
        log.info("User offline: {}", username);
    }
}
