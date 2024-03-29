package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.UserMapper;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UpdatePasswordRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;
import ua.kpi.mishchenko.mentoringsystem.entity.UserEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.RankRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.SpecializationRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.UserEmailProjection;
import ua.kpi.mishchenko.mentoringsystem.service.UserService;
import ua.kpi.mishchenko.mentoringsystem.service.security.JwtTokenService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.jpa.domain.Specification.where;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ua.kpi.mishchenko.mentoringsystem.entity.specification.UserSpecification.hoursGreaterThanMinValue;
import static ua.kpi.mishchenko.mentoringsystem.entity.specification.UserSpecification.hoursLessThanMaxValue;
import static ua.kpi.mishchenko.mentoringsystem.entity.specification.UserSpecification.matchRank;
import static ua.kpi.mishchenko.mentoringsystem.entity.specification.UserSpecification.matchRole;
import static ua.kpi.mishchenko.mentoringsystem.entity.specification.UserSpecification.matchSpecialization;
import static ua.kpi.mishchenko.mentoringsystem.entity.specification.UserSpecification.matchStatus;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.lessThanOne;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final int PAGE_SIZE = 6;

    private final UserRepository userRepository;
    private final RankRepository rankRepository;
    private final SpecializationRepository specializationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Override
    public UserDTO getUserById(Long userId) {
        log.debug("Getting user by id = [{}]", userId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Не вдається знайти даного користувача."));
        return userMapper.entityToDto(userEntity);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        log.debug("Getting user by email = [{}]", email);
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Не вдається знайти даного користувача."));
        return userMapper.entityToDto(userEntity);
    }

    @Override
    public PageBO<UserDTO> getUsers(UserFilter userFilter, int numberOfPage) {
        log.debug("Getting all users by filter");
        if (lessThanOne(numberOfPage)) {
            log.warn("The number of page and size of page must be greater than zero");
            throw new ResponseStatusException(BAD_REQUEST, "Номер сторінки не може бути менше 1.");
        }
        Page<UserEntity> userPage = userRepository.findAll(createSpecification(userFilter), PageRequest.of(numberOfPage - 1, PAGE_SIZE));
        if (!userPage.hasContent()) {
            log.debug("Cannot find users with this filter parameters");
            return new PageBO<>(numberOfPage, userPage.getTotalPages());
        }
        List<UserDTO> userDtos = userPage.getContent()
                .stream()
                .map(userMapper::entityToDto)
                .toList();
        return new PageBO<>(userDtos, numberOfPage, userPage.getTotalPages());
    }

    private Specification<UserEntity> createSpecification(UserFilter userFilter) {
        return where(matchSpecialization(userFilter.getSpecialization()))
                .and(matchRank(userFilter.getRank()))
                .and(matchStatus(userFilter.getStatus()))
                .and(matchRole(userFilter.getRole()))
                .and(hoursLessThanMaxValue(userFilter.getMaxHours()))
                .and(hoursGreaterThanMinValue(userFilter.getMinHours()));
    }

    private boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public List<String> getUsersEmailsFromChatByIdExceptSender(Long chatId, String senderEmail) {
        log.debug("Getting users from chat with id = [{}]", chatId);
        return userRepository.findAllByEmailNotAndChatsId(senderEmail, chatId).stream()
                .map(UserEmailProjection::getEmail)
                .toList();
    }

    @Override
    public Long getUserIdByEmail(String fromEmail) {
        return userRepository.findByEmail(fromEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Не вдається знайти такого користувача."))
                .getId();
    }

    @Override
    public void updateUserStatusById(Long userId, UserStatus status) {
        if (!existsById(userId)) {
            log.debug("Cannot find with id = [{}]", userId);
            throw new ResponseStatusException(NOT_FOUND, "Не вдається знайти такого користувача.");
        }
        userRepository.updateStatusByUserId(userId, status);
    }

    @Override
    public void updateUserPasswordByEmail(String email, UpdatePasswordRequest passwordRequest) {
        log.debug("Updating user password");
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Не вдається знайти такого користувчча."));
        String userPassword = userEntity.getPassword();
        String oldPassword = passwordRequest.getOldPassword();
        String newPassword = passwordRequest.getNewPassword();
        if (passwordsEqual(newPassword, userPassword) || newPassword.equals(oldPassword)) {
            throw new ResponseStatusException(BAD_REQUEST, "Старий та новий паролі не повинні співпадати.");
        }
        if (!passwordsEqual(oldPassword, userPassword)) {
            throw new ResponseStatusException(BAD_REQUEST, "Старий пароль введено неправильно.");
        }
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
        jwtTokenService.invalidateTokenByUserEmail(email);
    }

    private boolean passwordsEqual(String oldPassword, String userPassword) {
        return passwordEncoder.matches(oldPassword, userPassword);
    }

    @Override
    public List<String> getAllChatUsersByChatId(Long chatId) {
        log.debug("Getting all chat users by chat id = [{}]", chatId);
        List<UserEmailProjection> userEmailsByChatId = userRepository.findAllUserEmailsByChatId(chatId);
        if (userEmailsByChatId.isEmpty()) {
            return new ArrayList<>();
        }
        return userEmailsByChatId.stream()
                .map(UserEmailProjection::getEmail)
                .toList();
    }
}
