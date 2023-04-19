package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;
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
        UserDTO user = mentoringSystemFacade.getUserById(userId);
        if (isNull(user)) {
            responseBody.put("message", "Не вдається знайти даного користувача.");
            return new ResponseEntity<>(responseBody, NOT_FOUND);
        }
        responseBody.put("user", user);
        return new ResponseEntity<>(responseBody, OK);
    }
}
