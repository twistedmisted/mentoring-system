package ua.kpi.mishchenko.mentoringsystem.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "mentoring_requests")
@Setter
@Getter
public class MentoringRequestEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_id", nullable = false)
    private UserEntity from;

    @ManyToOne
    @JoinColumn(name = "to_id", nullable = false)
    private UserEntity to;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MentoringRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}
