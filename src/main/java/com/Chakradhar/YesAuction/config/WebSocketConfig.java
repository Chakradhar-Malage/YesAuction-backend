package com.Chakradhar.YesAuction.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");  // simple in-memory for now
        // Later: switch to Redis for scalability
        // config.enableStompBrokerRelay("/topic").setRelayHost("localhost").setRelayPort(61613);
        
        config.setApplicationDestinationPrefixes("/app");  // client sends to /app
        config.enableStompBrokerRelay("/topic", "/queue")
        .setRelayHost("localhost")
        .setRelayPort(61613);
        config.setUserDestinationPrefix("/user");  // for private messages if needed
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // adjust for prod (CORS)
                .withSockJS();  // fallback for older browsers
    }
}