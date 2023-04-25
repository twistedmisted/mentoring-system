package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.validation.SetSize;
import ua.kpi.mishchenko.mentoringsystem.validation.UniqueSetElements;

import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
public class QuestionnaireDTO {

    @JsonIgnore
    private Long userId;

    @NotBlank(message = "Необхідно заповнити інформацію про себе.")
    @Size(min = 250, max = 1024, message = "Інформація про себе необхідна містити від 250 до 1024 симолів.")
    private String about;

    @NotEmpty(message = "Необхідно додати від 3 до 20 навичок.")
    @SetSize(min = 3, max = 20, message = "Необхідно додати від 3 до 20 навичок.")
    @UniqueSetElements(message = "Не можливо зберегти навички, оскільки виявлено дублювання.")
    private Set<String> skills;

    @JsonInclude(NON_EMPTY)
    private Set<String> companies;

    @NotBlank(message = "Необхідно обрати свій рівень.")
    private String rank;

    @NotBlank(message = "Необхідно обрати свою спеціалізацію.")
    private String specialization;

    @JsonInclude(NON_NULL)
    private String linkedin;

    @NotNull(message = "Необхідно заповнити кількість годин на годину, які Ви готові приділяти.")
    @Max(value = 168, message = "Ваше значення занадто велике, спробуйте обрати інше. Максимальна кілкість годин у тижні - 168.")
    private Integer hoursPerWeek;
}
