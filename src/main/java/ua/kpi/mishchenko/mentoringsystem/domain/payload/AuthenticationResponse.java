package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.Data;

@Data
public class AuthenticationResponse {

    private String accessToken;
}
