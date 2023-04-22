package ua.kpi.mishchenko.mentoringsystem.domain.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFilter {

    private String specialization;
    private Double hoursPerWeek;
    private String rank;
    private UserStatus status;
    private String role;
}
