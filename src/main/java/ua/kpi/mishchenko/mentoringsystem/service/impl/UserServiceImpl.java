package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.QuestionnaireDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.QuestionnaireEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.RankEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.SpecializationEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.UserEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.UserMapper;
import ua.kpi.mishchenko.mentoringsystem.repository.RankRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.SpecializationRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;
import ua.kpi.mishchenko.mentoringsystem.service.UserService;
import ua.kpi.mishchenko.mentoringsystem.service.security.JwtTokenService;

import static java.util.Objects.isNull;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus.ACTIVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RankRepository rankRepository;
    private final SpecializationRepository specializationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Override
    public UserDTO getUserById(Long userId) {
        log.debug("Getting user by id = [{}]", userId);
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        return userMapper.entityToDto(userEntity);
    }

    @Override
    @Transactional
    public void updateUserById(Long userId, UserDTO userDTO) {
        log.debug("Updating user information by id = [{}]", userId);
        if (!existsById(userId)) {
            log.warn("The user with id = [{}] does not exist", userId);
            throw new IllegalArgumentException("Cannot find user with id = [" + userId + "]");
        }
        UserEntity userEntity = userRepository.findById(userId).get();
        updateUserInformation(userDTO, userEntity);
        userRepository.save(userEntity);
    }

    private boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    private void updateUserInformation(UserDTO userDTO, UserEntity userEntity) {
        if (valueExists(userDTO.getName())) {
            userEntity.setName(userDTO.getName());
        }
        if (valueExists(userDTO.getSurname())) {
            userEntity.setSurname(userDTO.getSurname());
        }
        if (valueExists(userDTO.getPassword())) {
            jwtTokenService.invalidateTokenByUserEmail(userEntity.getEmail());
            userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (valueExists(userDTO.getEmail()) && valuesNotEqual(userEntity.getEmail(), userDTO.getEmail())) {
            jwtTokenService.invalidateTokenByUserEmail(userEntity.getEmail());
            userEntity.setEmail(userDTO.getEmail());
        }
        QuestionnaireDTO questionnaire = userDTO.getQuestionnaire();
        if (valueExists(questionnaire)) {
            if (isNull(userEntity.getQuestionnaire())) {
                QuestionnaireEntity questionnaireEntity = new QuestionnaireEntity();
                questionnaireEntity.setUser(userEntity);
                questionnaireEntity.setUserId(userEntity.getId());
                userEntity.setQuestionnaire(questionnaireEntity);
            }
            if (valueExists(questionnaire.getAbout())) {
                userEntity.getQuestionnaire().setAbout(questionnaire.getAbout());
            }
            if (valueExists(questionnaire.getSkills())) {
                userEntity.getQuestionnaire().setSkills(questionnaire.getSkills());
            }
            if (valueExists(questionnaire.getSpecialization())) {
                userEntity.getQuestionnaire().setSpecialization(getSpecializationEntity(questionnaire));
            }
            if (valueExists(questionnaire.getRank())) {
                userEntity.getQuestionnaire().setRank(getRankEntity(questionnaire));
            }
            if (valueExists(questionnaire.getCompanies())) {
                userEntity.getQuestionnaire().setCompanies(questionnaire.getCompanies());
            }
            if (valueExists(questionnaire.getLinkedin())) {
                userEntity.getQuestionnaire().setLinkedin(questionnaire.getLinkedin());
            }
            if (valueExists(questionnaire.getHoursPerWeek())) {
                userEntity.getQuestionnaire().setHoursPerWeek(questionnaire.getHoursPerWeek());
            }
            userEntity.setStatus(ACTIVE);
        }
    }

    private boolean valuesNotEqual(String oldEmail, String newEmail) {
        return !oldEmail.equals(newEmail);
    }

    private SpecializationEntity getSpecializationEntity(QuestionnaireDTO questionnaire) {
        return specializationRepository.findByName(questionnaire.getSpecialization()).get();
    }

    private RankEntity getRankEntity(QuestionnaireDTO questionnaire) {
        return rankRepository.findByName(questionnaire.getRank()).get();
    }

    private boolean valueExists(Object value) {
        if (isNull(value)) {
            return false;
        }
        if (value instanceof String) {
            return !((String) value).isBlank();
        }
        return true;
    }

    @Override
    public boolean existsByIdAndEmail(Long id, String email) {
        return userRepository.existsByIdAndEmail(id, email);
    }
}
