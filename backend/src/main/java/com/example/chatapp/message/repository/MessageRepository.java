package com.example.chatapp.message.repository;

import com.example.chatapp.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findTop50ByConversation_IdOrderBySentAtAsc(UUID conversationId);
}
