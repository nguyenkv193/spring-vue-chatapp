package com.example.chatapp.conversation.repository;

import com.example.chatapp.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
            select c
            from Conversation c
            join ConversationMember cm on cm.conversation = c
            where cm.user.id = :userId
            order by c.updatedAt desc
            """)
    List<Conversation> findAllByMemberUserId(UUID userId);
}
