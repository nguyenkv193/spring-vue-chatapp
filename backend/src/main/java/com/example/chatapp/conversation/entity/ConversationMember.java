package com.example.chatapp.conversation.entity;

import com.example.chatapp.common.entity.BaseEntity;
import com.example.chatapp.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "conversation_members",
        uniqueConstraints = @UniqueConstraint(name = "uk_conversation_member", columnNames = {"conversation_id", "user_id"})
)
public class ConversationMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConversationMemberRole role = ConversationMemberRole.MEMBER;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;
}
