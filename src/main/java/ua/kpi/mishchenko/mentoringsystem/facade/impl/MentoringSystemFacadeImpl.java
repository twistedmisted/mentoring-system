package ua.kpi.mishchenko.mentoringsystem.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentoringSystemFacadeImpl implements MentoringSystemFacade {

    private final UserService userService;

    @Override
    public UserDTO getUserById(Long userId) {
        log.debug("Getting user by id = [{}]", userId);
        return userService.getUserById(userId);
    }
}
