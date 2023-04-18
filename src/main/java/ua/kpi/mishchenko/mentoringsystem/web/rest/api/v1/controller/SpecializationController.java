package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mishchenko.mentoringsystem.service.SpecializationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/specializations")
@RequiredArgsConstructor
@Slf4j
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSpecializationNames() {
        log.debug("Getting all specialization names");
        List<String> specializationNames = specializationService.getAllSpecializationNames();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("specializations", specializationNames);
        return new ResponseEntity<>(responseBody, OK);
    }
}
