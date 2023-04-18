package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDTO {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private UserStatus status;
    private List<RoleDTO> roles = new ArrayList<>();
    private QuestionnaireDTO questionnaire;

    public void addRole(RoleDTO role) {
        roles.add(role);
    }
}
