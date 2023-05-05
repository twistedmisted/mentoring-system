package ua.kpi.mishchenko.mentoringsystem.repository.projection;

import ua.kpi.mishchenko.mentoringsystem.domain.util.MessageStatus;

import java.sql.Timestamp;

public interface PrivateMessage {

    Long getId();

    String getText();

    Timestamp getCreatedAt();

    ChatUserProjection getChatUser();

    MessageStatus getStatus();
}
