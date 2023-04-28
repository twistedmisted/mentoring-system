package ua.kpi.mishchenko.mentoringsystem.domain.bo;

import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;

@Data
public class MessageBO {

    private Long id;
    private String text;
    private Long chatId;
    private UserDTO fromUser;
    private String date;
}
