package ua.kpi.mishchenko.mentoringsystem.repository.projection;

import ua.kpi.mishchenko.mentoringsystem.domain.util.ChatStatus;

import java.sql.Timestamp;

public interface PrivateChat {

    Long getId();

    String getTitle();

    Timestamp getLastMessageCreatedAt();

    Long getToUserId();

    String getLastMessageText();

    Integer getUnreadMessages();

    ChatStatus getStatus();
}
