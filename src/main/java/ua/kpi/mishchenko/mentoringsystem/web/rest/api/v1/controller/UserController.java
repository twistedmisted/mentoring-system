package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPassword;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPhoto;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;
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
@Validated
public class UserController {

    private static final String MENTOR_ROLE = "MENTOR";
    private static final String MENTEE_ROLE = "MENTEE";

    private static final UserStatus ACTIVE = UserStatus.ACTIVE;
    private final MentoringSystemFacade mentoringSystemFacade;

    @GetMapping(value = "/{userId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId, Principal principal) {
        log.debug("Getting user by id = [{}]", userId);
        Map<String, Object> responseBody = new HashMap<>();
        UserWithPhoto user = mentoringSystemFacade.getUserWithPhotoById(userId);
        if (checkPermissionForNotActiveUser(principal, user.getEmail(), user.getStatus())) {
            throw new ResponseStatusException(NOT_FOUND, "Не вдається знайти даного користувача.");
        }
        responseBody.put("user", user);
        return new ResponseEntity<>(responseBody, OK);
    }

    private boolean checkPermissionForNotActiveUser(Principal principal, String email, UserStatus status) {
        return (isNull(principal) || !principal.getName().equals(email)) && !status.equals(ACTIVE);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('MENTOR', 'MENTEE')")
    public ResponseEntity<Map<String, Object>> getMeStatus(Principal principal) {
        log.debug("Getting status for user with email = [{}]", principal.getName());
        UserWithPhoto user = mentoringSystemFacade.getUserByEmail(principal.getName());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("user", user);
        return new ResponseEntity<>(responseBody, OK);
    }

    @GetMapping("/mentors")
    public ResponseEntity<Map<String, Object>> getActiveMentors(@RequestParam(required = false) String specialization,
                                                                @RequestParam(required = false) Integer hoursPerWeek,
                                                                @RequestParam(required = false) String rank,
                                                                @RequestParam(value = "page", required = false, defaultValue = "1") int numberOfPage) {
        Map<String, Object> responseBody = getUsersByFilter(UserFilter.builder()
                .specialization(specialization)
                .rank(rank)
                .status(ACTIVE)
                .role(MENTOR_ROLE)
                .minHours(hoursPerWeek)
                .build(), numberOfPage);
        return new ResponseEntity<>(responseBody, OK);
    }

    @GetMapping("/mentees")
    public ResponseEntity<Map<String, Object>> getActiveMentees(@RequestParam(required = false) String specialization,
                                                                @RequestParam(required = false) Integer hoursPerWeek,
                                                                @RequestParam(required = false) String rank,
                                                                @RequestParam(value = "page", required = false, defaultValue = "1") int numberOfPage) {
        Map<String, Object> responseBody = getUsersByFilter(UserFilter.builder()
                .specialization(specialization)
                .rank(rank)
                .status(ACTIVE)
                .role(MENTEE_ROLE)
                .maxHours(hoursPerWeek)
                .build(), numberOfPage);
        return new ResponseEntity<>(responseBody, OK);
    }

    private Map<String, Object> getUsersByFilter(UserFilter userFilter, int numberOfPage) {
        log.debug("Getting users with status = [{}] and role = [{}]", ACTIVE, MENTEE_ROLE);
        PageBO<UserWithPhoto> userPage = mentoringSystemFacade.getUsers(userFilter, numberOfPage);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("page", userPage);
        return responseBody;
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
