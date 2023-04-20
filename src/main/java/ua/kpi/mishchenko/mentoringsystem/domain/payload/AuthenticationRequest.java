package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank(message = "Необхідно ввести адресу електронної пошти.")
    @Email(message = "Адресу електронної пошти введено некоректно.")
    private String email;

    @NotBlank(message = "Необіхдно ввести пароль.")
    @Size(min = 10, max = 20, message = "Пароль введено некоректно.")
    private String password;
}