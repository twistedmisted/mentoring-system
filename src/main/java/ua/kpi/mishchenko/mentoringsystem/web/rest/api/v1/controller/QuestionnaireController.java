package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.QuestionnaireUpdateRequest;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;

import java.security.Principal;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/questionnaires")
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireController {

    private final MentoringSystemFacade mentoringSystemFacade;

    @PutMapping
    public ResponseEntity<?> updateQuestionnaire(@Valid @RequestPart(value = "questionnaire", required = false)
                                                 QuestionnaireUpdateRequest questionnaire,
                                                 @RequestPart(value = "photo", required = false)
                                                 MultipartFile photo,
                                                 Principal principal) {
        log.debug("Updating questionnaire for user with email = [{}]", principal.getName());
        mentoringSystemFacade.updateQuestionnaireByUserEmail(principal.getName(), questionnaire, photo);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @PutMapping("/delete-photo")
    public ResponseEntity<?> deleteProfilePhoto(Principal principal) {
        log.debug("Removing user profile photo by email = [{}]", principal.getName());
        mentoringSystemFacade.deleteProfilePhotoByUserEmail(principal.getName());
        return new ResponseEntity<>(NO_CONTENT);
    }
}
