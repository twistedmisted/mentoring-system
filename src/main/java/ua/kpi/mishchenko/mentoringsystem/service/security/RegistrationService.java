package ua.kpi.mishchenko.mentoringsystem.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.RegistrationRequest;
import ua.kpi.mishchenko.mentoringsystem.entity.UserEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.RoleRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus.NEEDS_INFORMATION;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.getTimestampNow;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegistrationRequest userToSave) {
        log.debug("Registering new user");
        if (existsByEmail(userToSave.getEmail())) {
            log.warn("The user with email = [{}] already exists", userToSave.getEmail());
            throw new ResponseStatusException(BAD_REQUEST, "Дана пошта вже зареєстрована, спробуйте іншу.");
        }
        userRepository.save(createUserEntity(userToSave));
        log.debug("User was successfully saved");
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserEntity createUserEntity(RegistrationRequest userToSave) {
        if (isNull(userToSave)) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setName(userToSave.getName());
        entity.setSurname(userToSave.getSurname());
        entity.setEmail(userToSave.getEmail());
        entity.setPassword(passwordEncoder.encode(userToSave.getPassword()));
        entity.setRole(roleRepository.findByName(userToSave.getRole())
                .orElseThrow(() -> new ResponseStatusException(INTERNAL_SERVER_ERROR, "Схоже, що ми маємо деякі проблеми під час реєстрації. Зачекайте, можливо ми вже вирішуємо це.")));
        entity.setStatus(NEEDS_INFORMATION);
        entity.setCreatedAt(getTimestampNow());
        return entity;
    }
}
