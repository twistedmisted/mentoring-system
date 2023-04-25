package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UpdatePasswordRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;

public interface UserService {

    UserDTO getUserById(Long userId);

    UserDTO getUserByEmail(String email);

    PageBO<UserDTO> getUsers(UserFilter userFilter, int numberOfPage);

    boolean existsByIdAndEmail(Long id, String email);

    Long getUserIdByEmail(String fromEmail);

    void updateUserStatusById(Long userId, UserStatus status);

    void updateUserPasswordByEmail(String email, UpdatePasswordRequest passwordRequest);
}
