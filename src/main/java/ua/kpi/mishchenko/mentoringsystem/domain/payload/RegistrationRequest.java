package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotBlank(message = "Необхідно ввести ім'я користувача.")
    @Size(min = 2, max = 50, message = "Ім'я користувача має бути від 2 до 50 символів.")
    private String name;

    @NotBlank(message = "Необхідно ввести прізвище.")
    @Size(min = 2, max = 50, message = "Прізвище має бути від 2 до 50 символів.")
    private String surname;

    @NotBlank(message = "Необіхдно ввести електронну пошту.")
    @Email(message = "Електронну пошту введено некоректно.")
    private String email;

    @NotBlank(message = "Необхідно ввести пароль.")
    @Size(min = 10, max = 20, message = "Пароль має бути від 10 до 20 символів.")
    private String password;

    @NotBlank(message = "Необхідно обрати тип профілю.")
    private String role;
}