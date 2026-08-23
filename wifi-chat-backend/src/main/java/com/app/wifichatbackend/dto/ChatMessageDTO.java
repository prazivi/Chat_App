package com.app.wifichatbackend.dto;


import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long id;                    // Message ID (set after saving to DB)
    private String senderUsername;       // Who sent it
    private Long chatRoomId;            // Which room
    private String content;             // The message text
    private String messageType;         // TEXT, IMAGE, FILE
    private String status;              // SENT, DELIVERED, READ
    private LocalDateTime timestamp;    // When it was sent
}
