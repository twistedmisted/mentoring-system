package ua.kpi.mishchenko.mentoringsystem.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.PasswordMessageInfo;
import ua.kpi.mishchenko.mentoringsystem.entity.UserEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;
import ua.kpi.mishchenko.mentoringsystem.service.mail.EmailService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestorePasswordService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public void restorePasswordByEmail(String email) {
        log.debug("Restoring password for user with email = [{}]", email);
        if (!existsByEmail(email)) {
            log.debug("The user with email = [{}] does not exist", email);
            throw new ResponseStatusException(NOT_FOUND, "Користувача з даною поштою не знайдено. " +
                    "Переконайтеся в правильності вводу.");
        }
        log.debug("Generating and saving new password");
        UserEntity userEntity = userRepository.findByEmail(email).get();
        String newPassword = generateNewPassword();
        saveNewPassword(userEntity, newPassword);
        jwtTokenService.invalidateTokenByUserEmail(email);
        sendMessageToUser(userEntity, newPassword);
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private String generateNewPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        return pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private void saveNewPassword(UserEntity userEntity, String newPassword) {
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    private void sendMessageToUser(UserEntity user, String newPassword) {
        emailService.sendRestorePasswordMessage(PasswordMessageInfo.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .password(newPassword)
                .build());
    }
}
