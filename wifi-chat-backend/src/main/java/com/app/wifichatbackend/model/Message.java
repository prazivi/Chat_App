package com.app.wifichatbackend.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;              // Who sent it (User.id)

    @Column(nullable = false)
    private Long chatRoomId;            // Which room it was sent to (ChatRoom.id)

    @Column(columnDefinition = "TEXT", nullable = false)  // TEXT = unlimited length in PostgreSQL
    private String content;

    private String messageType;          // "TEXT", "IMAGE", "FILE"
    private String status;               // "SENT", "DELIVERED", "READ"

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.messageType == null) this.messageType = "TEXT";
        if (this.status == null) this.status = "SENT";
    }
}
