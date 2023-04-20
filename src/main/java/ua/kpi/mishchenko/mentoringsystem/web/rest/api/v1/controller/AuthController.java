package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

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
import ua.kpi.mishchenko.mentoringsystem.domain.payload.AuthenticationRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.AuthenticationResponse;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.RegistrationRequest;
import ua.kpi.mishchenko.mentoringsystem.service.security.JwtTokenService;
import ua.kpi.mishchenko.mentoringsystem.service.security.RegistrationService;

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

    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest user) {
        log.debug("Registration user with email = [{}]", user.getEmail());
        registrationService.registerUser(user);
        return new ResponseEntity<>(CREATED);
    }
}
