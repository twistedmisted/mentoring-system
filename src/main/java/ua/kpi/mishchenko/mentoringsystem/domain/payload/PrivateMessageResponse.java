package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrivateMessageResponse {

    private Long id;
    private String text;
    private ChatUser from;
    private String date;
    private Long chatId;
}
