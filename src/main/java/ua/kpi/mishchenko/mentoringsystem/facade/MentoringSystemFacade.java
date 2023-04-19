package ua.kpi.mishchenko.mentoringsystem.facade;

import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;

public interface MentoringSystemFacade {

    UserDTO getUserById(Long userId);
}
