package com.app.wifichatbackend.controller;



import com.app.wifichatbackend.dto.ChatMessageDTO;
import com.app.wifichatbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller               // Not @RestController! WebSocket uses @Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    // SimpMessagingTemplate = the tool to SEND messages to WebSocket subscribers

    // ──────────────────────────────────────────────────
    //  Handle messages sent to /app/chat.send
    //  (Group chat — broadcast to entire room)
    // ──────────────────────────────────────────────────
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO message, Principal principal) {
        // @Payload = the message body (JSON auto-parsed to ChatMessageDTO)
        // Principal = the authenticated user (from JWT)

        // Set the sender from the authenticated user (don't trust client-sent username)
        message.setSenderUsername(principal.getName());

        // Save to database
        ChatMessageDTO saved = chatService.processMessage(message);

        // Broadcast to all users subscribed to /topic/chatroom.{id}
        String destination = "/topic/chatroom." + message.getChatRoomId();
        messagingTemplate.convertAndSend(destination, saved);

        log.info("Message broadcast to {}: {}", destination, saved.getContent());
    }

    // ──────────────────────────────────────────────────
    //  Handle private messages sent to /app/chat.private
    //  (1-on-1 — only sender and receiver see it)
    // ──────────────────────────────────────────────────
    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessageDTO message, Principal principal) {
        message.setSenderUsername(principal.getName());

        ChatMessageDTO saved = chatService.processMessage(message);

        // Send to the specific recipient's personal queue
        // convertAndSendToUser("pranav", "/queue/private", message)
        //   → actually sends to /user/pranav/queue/private
        messagingTemplate.convertAndSendToUser(
                message.getSenderUsername(),
                "/queue/private",
                saved
        );

        log.info("Private message from {} saved", principal.getName());
    }

    // ──────────────────────────────────────────────────
    //  Handle user announcing they're online
    // ──────────────────────────────────────────────────
    @MessageMapping("/chat.online")
    public void userOnline(Principal principal) {
        chatService.setUserOnline(principal.getName());

        // Broadcast to everyone that this user is now online
        messagingTemplate.convertAndSend(
                "/topic/status",
                principal.getName() + " is online"
        );
    }
}
