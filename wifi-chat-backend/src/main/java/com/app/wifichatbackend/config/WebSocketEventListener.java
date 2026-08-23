package com.app.wifichatbackend.config;


import com.app.wifichatbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // Called when a user's WebSocket connection is established
    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {
        log.info("New WebSocket connection established");
    }

    // Called when a user disconnects (closes browser, loses WiFi, etc.)
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = null;
        if (headerAccessor.getUser() != null) {
            username = headerAccessor.getUser().getName();
        }

        if (username != null) {
            log.info("User disconnected: {}", username);

            // Update status in database
            chatService.setUserOffline(username);

            // Broadcast to everyone
            messagingTemplate.convertAndSend(
                    "/topic/status",
                    username + " went offline"
            );
        }
    }
}
