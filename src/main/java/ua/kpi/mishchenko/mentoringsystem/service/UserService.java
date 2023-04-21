package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;

public interface UserService {

    UserDTO getUserById(Long userId);

    UserStatus getUserStatusByEmail(String email);

    PageBO<UserDTO> getUsers(UserFilter userFilter, int numberOfPage);

    void updateUserById(Long userId, UserDTO userDTO);

    boolean existsByIdAndEmail(Long id, String email);
}
