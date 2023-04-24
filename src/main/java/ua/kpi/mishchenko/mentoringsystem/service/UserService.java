package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;

public interface UserService {

    UserDTO getUserById(Long userId);

    UserDTO getUserByEmail(String email);

    PageBO<UserDTO> getUsers(UserFilter userFilter, int numberOfPage);

    void updateUserById(Long userId, UserDTO userDTO);

    boolean existsByIdAndEmail(Long id, String email);

    Long getUserIdByEmail(String fromEmail);
}
