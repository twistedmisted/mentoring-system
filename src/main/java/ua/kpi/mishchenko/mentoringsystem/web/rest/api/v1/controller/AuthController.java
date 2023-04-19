package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.AuthenticationRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.AuthenticationResponse;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.RegistrationRequest;
import ua.kpi.mishchenko.mentoringsystem.service.security.JwtTokenService;
import ua.kpi.mishchenko.mentoringsystem.service.security.RegistrationService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Authorization", description = "The Authorization REST API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;


    @Operation(
            summary = "User authorization",
            description = "Authorize user and get JWT token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User was successfully authorized"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    )
            }
    )
    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody @Valid final AuthenticationRequest authenticationRequest) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (final BadCredentialsException ex) {
            throw new ResponseStatusException(UNAUTHORIZED, "Неправильно введено пошту або пароль, первірте правильність вводу.");
        }
        final AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(jwtTokenService.generateToken(authenticate));
        return new ResponseEntity<>(authenticationResponse, OK);
    }

    @Operation(
            summary = "User registration",
            description = "Register user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User was successfully registered"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "1. The email is already registered",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    )
            }
    )
    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest user) {
        log.debug("Registration user with email = [{}]", user.getEmail());
        registrationService.registerUser(user);
        return new ResponseEntity<>(CREATED);
    }
}
