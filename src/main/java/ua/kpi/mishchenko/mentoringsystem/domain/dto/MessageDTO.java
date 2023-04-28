package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MessageDTO {

    private Long id;
    private String text;
    private Long chatId;
    private UserDTO fromUser;
    private Timestamp createdAt;
}
