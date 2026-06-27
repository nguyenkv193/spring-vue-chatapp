package com.example.chatapp.message.repository;

import com.example.chatapp.message.entity.Message;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @EntityGraph(attributePaths = {"conversation", "sender"})
    List<Message> findTop50ByConversation_IdOrderBySentAtAsc(UUID conversationId);

    Optional<Message> findFirstByConversation_IdOrderBySentAtDesc(UUID conversationId);
}