package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.AuthenticationRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.AuthenticationResponse;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.RegistrationRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.RestorePasswordRequest;
import ua.kpi.mishchenko.mentoringsystem.service.security.JwtTokenService;
import ua.kpi.mishchenko.mentoringsystem.service.security.RegistrationService;
import ua.kpi.mishchenko.mentoringsystem.service.security.RestorePasswordService;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RestorePasswordService restorePasswordService;

    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody @Valid final AuthenticationRequest authenticationRequest) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (final BadCredentialsException ex) {
            throw new ResponseStatusException(UNAUTHORIZED, "Неправильно введено пошту або пароль, первірте правильність вводу.");
        }
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(jwtTokenService.generateToken(authenticate));
        return new ResponseEntity<>(authenticationResponse, OK);
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader(AUTHORIZATION) String authHeader) {
        final String token = authHeader.substring(7);
        jwtTokenService.invalidateTokenByUserToken(token);
        return new ResponseEntity<>(OK);
    }

    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest user) {
        log.debug("Registration user with email = [{}]", user.getEmail());
        registrationService.registerUser(user);
        return new ResponseEntity<>(CREATED);
    }

    @PostMapping("/restore-password")
    public ResponseEntity<Map<String, Object>> restorePassword(@Valid @RequestBody RestorePasswordRequest restorePasswordRequest) {
        log.debug("Restoring user password by email = [{}]", restorePasswordRequest.getEmail());
        restorePasswordService.restorePasswordByEmail(restorePasswordRequest.getEmail());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("detail", "Новий пароль надіслано на пошту.");
        return new ResponseEntity<>(responseBody, OK);
    }

    @GetMapping("/validate-token")
    public boolean validateToken(@RequestHeader(AUTHORIZATION) String authHeader) {
        log.debug("Token validation");
        final String token = authHeader.substring(7);
        return jwtTokenService.validateToken(token);
    }
}
