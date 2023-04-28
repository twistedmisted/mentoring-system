package ua.kpi.mishchenko.mentoringsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "users")
@Setter
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @OneToOne(mappedBy = "user", cascade = ALL)
    @PrimaryKeyJoinColumn
    private QuestionnaireEntity questionnaire;

    @OneToMany(mappedBy = "from", fetch = LAZY)
    private List<MentoringRequestEntity> requestsFrom = new ArrayList<>();

    @OneToMany(mappedBy = "to", fetch = LAZY)
    private List<MentoringRequestEntity> requestsTo = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    private Set<ChatEntity> chats = new HashSet<>();
}
