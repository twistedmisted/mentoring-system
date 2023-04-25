package ua.kpi.mishchenko.mentoringsystem.domain.bo;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class QuestionnaireBO {

    private String about = "";
    private Set<String> skills = new HashSet<>();
    private Set<String> companies = new HashSet<>();
    private String rank = "";
    private String specialization = "";
    private String linkedin = "";
    private int hoursPerWeek;
    private String profilePhotoUrl;
}
