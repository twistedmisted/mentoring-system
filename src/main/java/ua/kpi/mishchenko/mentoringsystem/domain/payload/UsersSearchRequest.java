package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.Data;

import java.util.List;

@Data
public class UsersSearchRequest {

    private List<String> skills;
    private String rank;
    private Double hoursPerWeek;
}
