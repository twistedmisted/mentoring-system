package ua.kpi.mishchenko.mentoringsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.ChatStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "chats")
@Setter
@Getter
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatStatus status;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @ManyToMany
    @JoinTable(name = "chat_members",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> users = new HashSet<>();

    @OneToMany(mappedBy = "chat", fetch = EAGER, cascade = ALL)
    private List<MessageEntity> messages = new ArrayList<>();

    @OneToMany(mappedBy = "chat", fetch = EAGER, cascade = ALL)
    private List<MentoringRequestEntity> mentoringRequests = new ArrayList<>();

    public void addMentoringRequest(MentoringRequestEntity mentoringRequest) {
        mentoringRequest.setChat(this);
        mentoringRequests.add(mentoringRequest);
    }

    public void addMentoringRequests(List<MentoringRequestEntity> mentoringRequests) {
        for (MentoringRequestEntity mentoringRequest : mentoringRequests) {
            addMentoringRequest(mentoringRequest);
        }
    }
}
