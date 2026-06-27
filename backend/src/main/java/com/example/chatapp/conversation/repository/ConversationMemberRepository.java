package com.example.chatapp.conversation.repository;

import com.example.chatapp.conversation.entity.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, UUID> {

    List<ConversationMember> findByUser_Id(UUID userId);
}
