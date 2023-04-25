package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "Необхідно ввести старий пароль.")
    @Size(min = 8, max = 20, message = "Старий пароль введено некоректно.")
    private String oldPassword;

    @NotBlank(message = "Необхідно ввести новий пароль.")
    @Size(min = 8, max = 20, message = "Новий пароль має бути від 8 до 20 символів.")
    private String newPassword;
}
