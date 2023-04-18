package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private Long id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private List<RoleDTO> roles;
    private QuestionnaireDTO questionnaire;
}
