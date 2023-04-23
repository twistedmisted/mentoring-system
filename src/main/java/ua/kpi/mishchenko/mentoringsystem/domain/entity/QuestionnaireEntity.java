package ua.kpi.mishchenko.mentoringsystem.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questionnaires")
@Setter
@Getter
public class QuestionnaireEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "about", nullable = false)
    private String about;

    @ElementCollection
    private List<String> skills = new ArrayList<>();

    @ElementCollection
    private List<String> companies = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "rank_id", nullable = false)
    private RankEntity rank;

    @ManyToOne
    @JoinColumn(name = "specialization_id", nullable = false)
    private SpecializationEntity specialization;

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "hours_per_week", nullable = false)
    private Integer hoursPerWeek;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
