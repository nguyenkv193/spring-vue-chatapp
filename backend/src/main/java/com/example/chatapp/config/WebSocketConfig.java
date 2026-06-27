package com.example.chatapp.config;

import com.example.chatapp.auth.security.StompAuthChannelInterceptor;
import com.example.chatapp.common.constants.ApplicationConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final String[] allowedOrigins;
    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    public WebSocketConfig(
            @Value("${app.websocket.allowed-origins}") String allowedOrigins,
            StompAuthChannelInterceptor stompAuthChannelInterceptor
    ) {
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toArray(String[]::new);
        this.stompAuthChannelInterceptor = stompAuthChannelInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(
                ApplicationConstants.WEBSOCKET_TOPIC_PREFIX,
                ApplicationConstants.WEBSOCKET_QUEUE_PREFIX
        );
        registry.setApplicationDestinationPrefixes(ApplicationConstants.WEBSOCKET_APP_PREFIX);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ApplicationConstants.WEBSOCKET_ENDPOINT)
                .setAllowedOrigins(allowedOrigins);
    }
}
