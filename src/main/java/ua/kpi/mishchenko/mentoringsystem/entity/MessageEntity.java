package ua.kpi.mishchenko.mentoringsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "messages")
@Setter
@Getter
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private UserEntity fromUser;
}
