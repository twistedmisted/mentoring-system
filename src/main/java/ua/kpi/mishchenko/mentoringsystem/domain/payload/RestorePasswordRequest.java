package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RestorePasswordRequest {

    @NotBlank(message = "Необхідно ввести адресу електронної пошти.")
    @Email(message = "Адресу електронної пошти введено некоректно.")
    private String email;
}
