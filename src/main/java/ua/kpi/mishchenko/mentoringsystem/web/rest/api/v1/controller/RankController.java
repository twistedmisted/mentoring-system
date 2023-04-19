package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "RankController", description = "The Rank REST API")
@RestController
@RequestMapping("/api/v1/ranks")
@RequiredArgsConstructor
@Slf4j
public class RankController {

    private final RankService rankService;

    @Operation(
            summary = "Get All Ranks",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The ranks were successfully received"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "The access was forbidden"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    )
            }
    )
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getAllRankNames() {
        log.debug("Getting all rank names");
        List<String> rankNames = rankService.getAllRankNames();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("ranks", rankNames);
        return new ResponseEntity<>(responseBody, OK);
    }
}
