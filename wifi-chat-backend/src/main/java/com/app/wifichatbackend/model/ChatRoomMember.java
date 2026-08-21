package com.app.wifichatbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_members")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long chatRoomId;       // Which room

    @Column(nullable = false)
    private Long userId;           // Which user

    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        this.joinedAt = LocalDateTime.now();
    }
}
