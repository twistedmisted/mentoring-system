package ua.kpi.mishchenko.mentoringsystem.domain.util;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserFilter {

    private List<String> specializations;
    private Double hoursPerWeek;
    private String rank;
    private UserStatus status;
    private String role;
}
