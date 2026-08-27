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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // ═════════════════════════════════════════════════════════════
    // CONCURRENCY TOOL #1: ConcurrentHashMap for online users
    // ═════════════════════════════════════════════════════════════
    // WHY: Regular HashMap is NOT thread-safe. If two users go online
    //      at the same time, HashMap can corrupt its internal structure.
    //      ConcurrentHashMap handles this automatically.
    //
    // HOW IT WORKS:
    //   - Internally divides the map into segments
    //   - Multiple threads can write to DIFFERENT segments simultaneously
    //   - Only locks the specific segment being modified
    //   - Reads never block (lock-free reads)

    // We use ConcurrentHashMap as a SET (using newKeySet())
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    // ═════════════════════════════════════════════════════════════
    // CONCURRENCY TOOL #2: Per-ChatRoom Locks
    // ═════════════════════════════════════════════════════════════
    // WHY: We want messages in the SAME room to be processed one at a time
    //      (to preserve order and prevent race conditions).
    //      But messages in DIFFERENT rooms should process in parallel
    //      (for performance).
    //
    // HOW:
    //   - Each chat room gets its own ReentrantLock
    //   - Room 1's lock doesn't block Room 2's processing
    //   - "Reentrant" means the same thread can acquire the lock multiple times

    private final ConcurrentHashMap<Long, ReentrantLock> chatRoomLocks = new ConcurrentHashMap<>();

    // ═════════════════════════════════════════════════════════════
    // Process a message with THREAD SAFETY
    // ═════════════════════════════════════════════════════════════
    @Transactional   // CONCURRENCY TOOL #3: All DB ops are atomic
    public ChatMessageDTO processMessage(ChatMessageDTO dto) {

        // Get (or create) the lock for this specific chat room
        // computeIfAbsent is thread-safe: only creates ONE lock per room
        ReentrantLock lock = chatRoomLocks.computeIfAbsent(
                dto.getChatRoomId(),
                id -> new ReentrantLock()
        );

        lock.lock();  // ← Acquire the lock. Other threads for THIS room wait here.
        try {
            // ── Everything inside here is SINGLE-THREADED per room ──

            // Find the sender
            User sender = userRepository.findByUsername(dto.getSenderUsername())
                    .orElseThrow(() -> new RuntimeException("User not found: " + dto.getSenderUsername()));

            // Build the message entity
            Message message = Message.builder()
                    .senderId(sender.getId())
                    .chatRoomId(dto.getChatRoomId())
                    .content(dto.getContent())
                    .messageType(dto.getMessageType() != null ? dto.getMessageType() : "TEXT")
                    .build();

            // Save to database (inside @Transactional, so it's atomic)
            Message saved = messageRepository.save(message);

            log.info("Message #{} saved: from={}, room={}, content='{}'",
                    saved.getId(), dto.getSenderUsername(), dto.getChatRoomId(), dto.getContent());

            // Return the saved message
            return ChatMessageDTO.builder()
                    .id(saved.getId())
                    .senderUsername(dto.getSenderUsername())
                    .chatRoomId(saved.getChatRoomId())
                    .content(saved.getContent())
                    .messageType(saved.getMessageType())
                    .status(saved.getStatus())
                    .timestamp(saved.getCreatedAt())
                    .build();

        } finally {
            lock.unlock();  // ← ALWAYS release the lock, even if an exception occurred
        }
    }

    // ═════════════════════════════════════════════════════════════
    // Online user tracking (all thread-safe via ConcurrentHashMap)
    // ═════════════════════════════════════════════════════════════

    public void setUserOnline(String username) {
        onlineUsers.add(username);                                    // Thread-safe add
        userRepository.updateStatus(username, User.UserStatus.ONLINE);
        log.info("User ONLINE: {} (total online: {})", username, onlineUsers.size());
    }

    public void setUserOffline(String username) {
        onlineUsers.remove(username);                                 // Thread-safe remove
        userRepository.updateStatus(username, User.UserStatus.OFFLINE);
        log.info("User OFFLINE: {} (total online: {})", username, onlineUsers.size());
    }

    public boolean isUserOnline(String username) {
        return onlineUsers.contains(username);                        // Thread-safe check
    }

    public Set<String> getOnlineUsers() {
        return Set.copyOf(onlineUsers);     // Return an immutable copy (safe to iterate)
    }
}