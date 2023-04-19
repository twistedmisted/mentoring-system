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
import ua.kpi.mishchenko.mentoringsystem.service.SpecializationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Specialization", description = "The Specialization REST API")
@RestController
@RequestMapping("/api/v1/specializations")
@RequiredArgsConstructor
@Slf4j
public class SpecializationController {

    private final SpecializationService specializationService;

    @Operation(
            summary = "Get All Specialization",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The specializations were successfully received"
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
    public ResponseEntity<Map<String, Object>> getAllSpecializationNames() {
        log.debug("Getting all specialization names");
        List<String> specializationNames = specializationService.getAllSpecializationNames();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("specializations", specializationNames);
        return new ResponseEntity<>(responseBody, OK);
    }
}
