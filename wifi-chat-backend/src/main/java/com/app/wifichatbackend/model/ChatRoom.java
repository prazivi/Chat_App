package com.app.wifichatbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;                  // Room name like "General" or "Team Alpha"

    @Enumerated(EnumType.STRING)
    private RoomType type;                // PRIVATE (2 people) or GROUP (many people)

    private LocalDateTime createdAt;

    public enum RoomType {
        PRIVATE,   // 1-on-1 chat
        GROUP      // Group chat
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
