package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import ua.kpi.mishchenko.mentoringsystem.validation.ListSize;

import java.util.List;

@Data
public class QuestionnaireUpdateRequest {

    @NotBlank(message = "Необхідно заповнити інформацію про себе.")
    @Size(min = 250, max = 1024, message = "Інформація про себе необхідна містити від 250 до 1024 симолів.")
    private String about;

    @NotEmpty(message = "Необхідно додати від 5 до 20 навичок.")
    @ListSize(min = 5, max = 20, message = "Необхідно додати від 5 до 20 навичок.")
    private List<String> skills;

    private List<String> companies;

    @NotBlank(message = "Необхідно обрати свій рівень.")
    private String rank;

    @NotBlank(message = "Необхідно обрати свою спеціалізацію.")
    private String specialization;

    @URL(message = "Посилання на профіль з LinkedIn некоректне.")
    private String linkedin;

    @NotNull(message = "Необхідно заповнити кількість годин на годину, які Ви готові приділяти.")
    @Max(value = 168, message = "Ваше значення занадто велике, спробуйте обрати інше. Максимальна кілкість годин у тижні - 168.")
    private Integer hoursPerWeek;
}
