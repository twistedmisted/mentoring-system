package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mishchenko.mentoringsystem.service.RankService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/ranks")
@RequiredArgsConstructor
@Slf4j
public class RankController {

    private final RankService rankService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRankNames() {
        log.debug("Getting all rank names");
        List<String> rankNames = rankService.getAllRankNames();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("ranks", rankNames);
        return new ResponseEntity<>(responseBody, OK);
    }
}
