package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionnaireDTO {

    private Long userId;
    private String about;
    private List<String> skills;
    private List<String> companies;
    private RankDTO rank;
    private SpecializationDTO specialization;
    private String linkedin;
    private Double hoursPerWeek;

    public void addSkill(String skill) {
        skills.add(skill);
    }

    public void removeSkill(String skill) {
        skills.remove(skill);
    }

    public void addCompany(String company) {
        companies.add(company);
    }

    public void removeCompany(String company) {
        companies.remove(company);
    }
}
