package ua.kpi.mishchenko.mentoringsystem.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "questionnaires")
@Setter
@Getter
public class QuestionnaireEntity {

    @Id
    @Column(name = "user_id")
    @NotNull(message = "Необхідно вказати ідентифікатор користувача.")
    private Long userId;

    @Column(name = "about", nullable = false)
    @NotNull(message = "Необхідно заповнити поле про себе.")
    private String about;

    @ElementCollection
    @CollectionTable(name = "skills",
            uniqueConstraints = @UniqueConstraint(columnNames = {"questionnaire_entity_user_id", "skills"}))
    @NotEmpty(message = "Необхідно додати навички.")
    private Set<String> skills = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "companies",
            uniqueConstraints = @UniqueConstraint(columnNames = {"questionnaire_entity_user_id", "companies"}))
    private Set<String> companies = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "rank_id", nullable = false)
    private RankEntity rank;

    @ManyToOne
    @JoinColumn(name = "specialization_id", nullable = false)
    private SpecializationEntity specialization;

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "hours_per_week", nullable = false)
    @NotNull(message = "Необхідно вказати години на тиждень для навчання.")
    private Integer hoursPerWeek;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
