package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPassword;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPhoto;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final MentoringSystemFacade mentoringSystemFacade;

    @GetMapping(value = "/{userId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        log.debug("Getting user by id = [{}]", userId);
        Map<String, Object> responseBody = new HashMap<>();
        UserWithPhoto user = mentoringSystemFacade.getUserWithPhotoById(userId);
        if (isNull(user)) {
            throw new ResponseStatusException(NOT_FOUND, "Не вдається знайти даного користувача.");
        }
        responseBody.put("user", user);
        return new ResponseEntity<>(responseBody, OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserById(@PathVariable Long userId,
                                                              @Valid @RequestPart(value = "user", required = false) UserWithPassword user,
                                                              @RequestPart(value = "photo", required = false) MultipartFile photo,
                                                              Principal principal) {
        log.debug("Updating user by id = [{}]", userId);
        if (!mentoringSystemFacade.checkIfIdAndEmailMatch(userId, principal.getName())) {
            return new ResponseEntity<>(FORBIDDEN);
        }
        mentoringSystemFacade.updateUserById(userId, user, photo);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
