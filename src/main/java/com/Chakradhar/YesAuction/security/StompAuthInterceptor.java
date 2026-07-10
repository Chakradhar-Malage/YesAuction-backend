package com.Chakradhar.YesAuction.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collections;

/**
 * Runs on every STOMP CONNECT frame. Reads the "Authorization" native header
 * (sent by the frontend as connectHeaders: { Authorization: "Bearer <token>" }),
 * validates it, and attaches a Principal to the session so that
 * SimpMessagingTemplate.convertAndSendToUser(username, ...) can find it.
 *
 * Intentionally lenient: if no/invalid token is present, the connection is
 * still allowed through (so anonymous auction-topic subscriptions keep working) —
 * it just won't be addressable via convertAndSendToUser.
 */
@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String username = jwtUtil.extractUsername(token);
                    if (username != null) {
                        Principal principal = new UsernamePasswordAuthenticationToken(
                                username, null, Collections.emptyList());
                        accessor.setUser(principal);
                    }
                } catch (Exception e) {
                    System.out.println("WS auth failed: " + e.getMessage());
                }
            }
        }
        return message;
    }
}