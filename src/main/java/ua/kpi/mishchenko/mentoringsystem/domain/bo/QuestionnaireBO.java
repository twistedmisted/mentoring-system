package ua.kpi.mishchenko.mentoringsystem.domain.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionnaireBO {

    private String about = "";
    private List<String> skills = new ArrayList<>();
    private List<String> companies = new ArrayList<>();
    private String rank = "";
    private String specialization = "";
    private String linkedin = "";
    private int hoursPerWeek;
    private String profilePhotoUrl;
}
