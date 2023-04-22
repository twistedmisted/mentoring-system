package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import lombok.Data;

@Data
public class ReviewDTO {

    private Long id;
    private String text;
    private Double rating;
    private Long userId;
}
