package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;

public interface UserService {

    UserDTO getUserById(Long userId);
}
