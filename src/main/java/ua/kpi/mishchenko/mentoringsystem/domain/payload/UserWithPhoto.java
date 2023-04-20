package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.Builder;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.QuestionnaireDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;

@Data
@Builder
public class UserWithPhoto {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private UserStatus status;
    private String role;
    private QuestionnaireDTO questionnaire;
    private String profilePhotoUrl;
}
