package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
public class UserDTO {

    private Long id;

    private String name;

    private String surname;

    private String email;

    @JsonIgnore
    private String password;

    private UserStatus status;

    private String role;

    @JsonInclude(NON_NULL)
    private QuestionnaireDTO questionnaire;
}
