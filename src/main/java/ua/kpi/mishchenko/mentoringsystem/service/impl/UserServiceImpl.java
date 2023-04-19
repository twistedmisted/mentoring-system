package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.UserEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.UserMapper;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;
import ua.kpi.mishchenko.mentoringsystem.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO getUserById(Long userId) {
        log.debug("Getting user by id = [{}]", userId);
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        return userMapper.entityToDto(userEntity);
    }
}
