package ua.kpi.mishchenko.mentoringsystem.domain.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.util.ChatStatus;

@Data
public class ChatBO {

    private Long id;

    private String title;

    @JsonProperty("avatar")
    private String photoUrl;

    @JsonProperty("subtitle")
    private String lastMessageText;

    @JsonProperty("date")
    private String lastMessageDate;

    @JsonProperty("unread")
    private int unreadMessages;

    private ChatStatus status;
}
