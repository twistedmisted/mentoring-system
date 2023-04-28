package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@Data
public class ChatDTO {

    private Long id;
    private Timestamp createdAt;
    private Set<UserDTO> users;
}
