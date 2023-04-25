package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.Builder;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.QuestionnaireBO;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;

@Data
@Builder
public class UserWithQuestionnaire {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private UserStatus status;
    private String createdAt;
    private String role;
    private double rating;
    private QuestionnaireBO questionnaire;
}
