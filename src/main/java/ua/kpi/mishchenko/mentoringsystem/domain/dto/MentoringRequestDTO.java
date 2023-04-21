package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;

import java.sql.Timestamp;

@Data
public class MentoringRequestDTO {

    private Long id;
    private UserDTO from;
    private UserDTO to;
    private MentoringRequestStatus status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
