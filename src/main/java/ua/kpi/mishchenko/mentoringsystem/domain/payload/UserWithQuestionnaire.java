package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.QuestionnaireBO;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

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
    private String rating;
    private QuestionnaireBO questionnaire;

    @JsonInclude(NON_NULL)
    private MentoringRequestPayload mentoringRequest;

    private boolean reviewAvailable;
}
