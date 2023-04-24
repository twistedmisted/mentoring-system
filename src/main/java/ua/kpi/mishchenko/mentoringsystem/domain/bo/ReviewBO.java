package ua.kpi.mishchenko.mentoringsystem.domain.bo;

import lombok.Data;

@Data
public class ReviewBO {

    private String name;
    private String surname;
    private String text;
    private Integer rating;
    private String createdAt;
}
