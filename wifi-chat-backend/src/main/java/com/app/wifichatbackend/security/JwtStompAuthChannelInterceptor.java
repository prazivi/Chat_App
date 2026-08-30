package com.app.wifichatbackend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtStompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() != StompCommand.CONNECT) {
            return message;
        }

        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            authorization = accessor.getFirstNativeHeader("authorization");
        }
        log.info("WebSocket CONNECT command={}, authHeaderPresent={}", accessor.getCommand(), StringUtils.hasText(authorization));
        if (StringUtils.hasText(authorization)) {
            log.info("WebSocket raw auth header='{}' length={}", authorization, authorization.length());
        }
        if (!StringUtils.hasText(authorization)) {
            throw new IllegalArgumentException("Missing or invalid websocket Authorization header");
        }

        String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
        log.info("WebSocket JWT token length={}", token.length());
        if (!tokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid websocket JWT token");
        }

        String username = tokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        accessor.setUser(new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        ));
        return message;
    }
}
