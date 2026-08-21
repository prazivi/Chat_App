package com.app.wifichatbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity                              // Tells JPA: "This class = a database table"
@Table(name = "users")               // Table name in PostgreSQL (can't use "user" — it's a reserved word)
@Data                                // Lombok: auto-generates getters, setters, toString, equals, hashCode
@NoArgsConstructor                   // Lombok: generates empty constructor (JPA requires this)
@AllArgsConstructor                  // Lombok: generates constructor with all fields
@Builder                             // Lombok: lets you do User.builder().username("x").build()
public class User {

    @Id                              // This field is the PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment (1, 2, 3...)
    private Long id;

    @Column(unique = true, nullable = false)   // UNIQUE + NOT NULL constraint
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;     // We NEVER store plain text passwords!

    private String displayName;      // The name shown in chat (nullable, defaults to username)

    @Enumerated(EnumType.STRING)     // Store enum as "ONLINE"/"OFFLINE" text, not 0/1 numbers
    @Column(nullable = false)
    private UserStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime lastSeen;

    // Enum = a fixed set of allowed values
    public enum UserStatus {
        ONLINE, OFFLINE
    }

    @PrePersist   // JPA calls this method automatically BEFORE saving a new row
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = UserStatus.OFFLINE;
    }
}
