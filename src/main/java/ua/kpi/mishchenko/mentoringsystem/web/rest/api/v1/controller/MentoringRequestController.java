package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.MentoringRequestBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MentoringRequestPayload;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/mentoring-requests")
@RequiredArgsConstructor
@Slf4j
public class MentoringRequestController {

    private final MentoringSystemFacade mentoringSystemFacade;

    @GetMapping("/from-me")
    public ResponseEntity<Map<String, Object>> getMentoringRequestsFromUserById(@RequestParam(required = false)
                                                                                String status,
                                                                                @RequestParam(
                                                                                        value = "page",
                                                                                        required = false,
                                                                                        defaultValue = "1")
                                                                                int numberOfPage,
                                                                                Principal principal) {
        log.debug("Getting mentoring requests from user with email = [{}]", principal.getName());
        Map<String, Object> responseBody = getMentoringReqPageByFilter(
                createMentoringReqFilter(status, principal.getName(), null), numberOfPage);
        return new ResponseEntity<>(responseBody, OK);
    }

    @GetMapping("/to-me")
    public ResponseEntity<Map<String, Object>> getMentoringRequestsToUserById(@RequestParam(required = false)
                                                                              String status,
                                                                              @RequestParam(
                                                                                      value = "page",
                                                                                      required = false,
                                                                                      defaultValue = "1")
                                                                              int numberOfPage,
                                                                              Principal principal) {
        log.debug("Getting mentoring requests from user with email = [{}]", principal.getName());
        Map<String, Object> responseBody = getMentoringReqPageByFilter(
                createMentoringReqFilter(status, null, principal.getName()), numberOfPage);
        return new ResponseEntity<>(responseBody, OK);
    }

    private Map<String, Object> getMentoringReqPageByFilter(MentoringRequestFilter filter, int numberOfPage) {
        PageBO<MentoringRequestPayload> mentoringRequestsPage = mentoringSystemFacade.getMentoringRequests(filter, numberOfPage);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("page", mentoringRequestsPage);
        return responseBody;
    }

    private static MentoringRequestFilter createMentoringReqFilter(String status, String fromEmail, String toEmail) {
        MentoringRequestFilter.MentoringRequestFilterBuilder filterBuilder = MentoringRequestFilter.builder()
                .fromEmail(fromEmail)
                .toEmail(toEmail);
        if (!isNull(status)) {
            try {
                filterBuilder.status(MentoringRequestStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("[{}] does not exist in the list of mentoring statuses", status);
                throw new ResponseStatusException(BAD_REQUEST, "Схоже такого статусу для запитів не існує.");
            }
        }
        return filterBuilder.build();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createMentoringRequest(@Valid @RequestBody MentoringRequestBO mentoringRequest,
                                                                      Principal principal) {
        log.debug("Creating new mentoring request");
        MentoringRequestPayload createdRequest = mentoringSystemFacade.createMentoringRequest(principal.getName(), mentoringRequest);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mentoringRequest", createdRequest);
        return new ResponseEntity<>(responseBody, CREATED);
    }

    @PutMapping("/accept/{reqId}")
    public ResponseEntity<Map<String, Object>> acceptMentoringRequest(@PathVariable Long reqId,
                                                                      Principal principal) {
        log.debug("Approving mentoring request by id = [{}]", reqId);
        MentoringRequestPayload mentoringReq = mentoringSystemFacade.acceptMentoringReq(reqId, principal.getName());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mentoringRequest", mentoringReq);
        return new ResponseEntity<>(responseBody, OK);
    }

    @PutMapping("/reject/{reqId}")
    public ResponseEntity<Map<String, Object>> rejectMentoringRequest(@PathVariable Long reqId,
                                                                      Principal principal) {
        log.debug("Rejecting mentoring request by id = [{}]", reqId);
        MentoringRequestPayload mentoringReq = mentoringSystemFacade.rejectMentoringReq(reqId, principal.getName());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mentoringRequest", mentoringReq);
        return new ResponseEntity<>(responseBody, OK);
    }

    @PutMapping("/cancel/{reqId}")
    public ResponseEntity<Map<String, Object>> cancelMentoringRequest(@PathVariable Long reqId,
                                                                      Principal principal) {
        log.debug("Canceling mentoring request by id = [{}]", reqId);
        MentoringRequestPayload mentoringReq = mentoringSystemFacade.cancelMentoringReq(reqId, principal.getName());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mentoringRequest", mentoringReq);
        return new ResponseEntity<>(responseBody, OK);
    }

    @PutMapping("/finish/{reqId}")
    public ResponseEntity<Map<String, Object>> finishMentoringRequest(@PathVariable Long reqId,
                                                                      Principal principal) {
        log.debug("Finishing mentoring request by id = [{}]", reqId);
        MentoringRequestPayload mentoringReq = mentoringSystemFacade.finishMentoringReq(reqId, principal.getName());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mentoringRequest", mentoringReq);
        return new ResponseEntity<>(responseBody, OK);
    }
}
