package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ReviewDTO {

    private Long id;
    private String text;
    private Integer rating;
    private Timestamp createdAt;
    private UserDTO fromUser;
    private UserDTO toUser;
}
