package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;

import java.sql.Timestamp;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "Необхідно ввести ім'я.")
    @Pattern(regexp = "^[А-ЯІіЇїЄєҐґ'\\-]{2,}$", message = "Ім'я має бути українською мовою та від 2 до 50 символів.")
    private String name;

    @NotBlank(message = "Необхідно ввести прізвище.")
    @Pattern(regexp = "^[А-ЯІіЇїЄєҐґ'\\-]{2,}$", message = "Прізвище має бути українською мовою та від 2 до 50 символів.")
    private String surname;

    @NotBlank(message = "Електронна пошта не може бути порожньою.")
    @Email(message = "Це не схоже на електронну пошту, перевірте правильність вводу.")
    private String email;

    @JsonIgnore
    private String password;

    private UserStatus status;

    private Timestamp createdAt;

    @NotBlank(message = "Необхідно обрати тип профілю.")
    private String role;

    @JsonInclude(NON_NULL)
    @NotNull(message = "Необхідно заповнити анкету.")
    private QuestionnaireDTO questionnaire;
}
