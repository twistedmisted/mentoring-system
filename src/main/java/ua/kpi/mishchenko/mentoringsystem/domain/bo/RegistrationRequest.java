package ua.kpi.mishchenko.mentoringsystem.domain.bo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "The name must be between 2 and 50 symbols")
    private String name;

    @NotBlank(message = "Surname is mandatory")
    @Size(min = 2, max = 50, message = "The surname must be between 2 and 50 symbols")
    private String surname;

    @NotBlank(message = "Name is mandatory")
    @Email(message = "This is not an email")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 2, max = 50, message = "The password must be between 10 and 20 symbols")
    private String password;

    private String role;
}
