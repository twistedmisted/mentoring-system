package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReviewRequest {

    @NotBlank(message = "Необхідно ввести текст до відгуку.")
    @Size(max = 1024, message = "Максимальна кількість символів відгуку становить 1024.")
    private String text;

    @NotNull(message = "Необхідно вказати ідентифікатор користувача, якому адресується відгук.")
    private Long toUserId;

    @NotNull(message = "Необхідно вказати оцінку до відгуку.")
    @Min(value = 1, message = "Необхідно поставити хоча б 1 зірочку.")
    @Max(value = 5, message = "Максимальна оцінка становить 5 зірочок.")
    private Integer rating;
}
