package com.example.chatapp.websocket.controller;

import com.example.chatapp.websocket.dto.ChatInboundMessage;
import com.example.chatapp.websocket.service.ChatMessagingService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWebSocketController {

    private final ChatMessagingService chatMessagingService;

    public ChatWebSocketController(ChatMessagingService chatMessagingService) {
        this.chatMessagingService = chatMessagingService;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Valid ChatInboundMessage message, Principal principal) {
        chatMessagingService.handleIncomingMessage(message, principal);
    }
}
