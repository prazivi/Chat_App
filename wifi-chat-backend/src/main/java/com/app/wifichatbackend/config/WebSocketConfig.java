package com.app.wifichatbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker    // Enables WebSocket with STOMP message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final com.app.wifichatbackend.security.JwtStompAuthChannelInterceptor jwtStompAuthChannelInterceptor;

    public WebSocketConfig(com.app.wifichatbackend.security.JwtStompAuthChannelInterceptor jwtStompAuthChannelInterceptor) {
        this.jwtStompAuthChannelInterceptor = jwtStompAuthChannelInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // Enable a simple in-memory message broker
        // Messages sent to destinations starting with these prefixes
        // are BROADCAST to all subscribed clients
        config.enableSimpleBroker(
                "/topic",    // For broadcasting to many users (group chat)
                "/queue"     // For sending to one specific user (private messages)
        );

        // When a CLIENT sends a message, the destination must start with /app
        // Spring will route it to a @MessageMapping method in your controllers
        config.setApplicationDestinationPrefixes("/app");

        // For user-specific messages (private chat)
        // When you send to /user/pranav/queue/private,
        // only the user "pranav" receives it
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the URL that clients connect to for WebSocket
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")   // Allow connections from any device on the network
                .withSockJS();                    // Enable SockJS fallback for browsers that don't support WebSocket
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtStompAuthChannelInterceptor);
    }
}
