package ua.kpi.mishchenko.mentoringsystem.domain.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFilter {

    private String specialization;
    private String rank;
    private UserStatus status;
    private String role;
    private Integer minHours;
    private Integer maxHours;
}
