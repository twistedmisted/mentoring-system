package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.ReviewBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.CreateReviewRequest;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final MentoringSystemFacade mentoringSystemFacade;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getReviewsByUserId(@RequestParam(value = "user") Long userId,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "1") int numberOfPage) {
        log.debug("Getting reviews by user id = [{}]", userId);
        PageBO<ReviewBO> page = mentoringSystemFacade.getReviewsByUserId(userId, numberOfPage);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("page", page);
        return new ResponseEntity<>(responseBody, OK);
    }

    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody CreateReviewRequest review,
                                          Principal principal) {
        log.debug("Creating new review for user with id = [{}] from = [{}]",
                review.getToUserId(), principal.getName());
        mentoringSystemFacade.createReview(review, principal.getName());
        return new ResponseEntity<>(CREATED);
    }
}
