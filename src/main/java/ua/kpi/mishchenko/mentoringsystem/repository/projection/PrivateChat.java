package ua.kpi.mishchenko.mentoringsystem.repository.projection;

import java.sql.Timestamp;

public interface PrivateChat {

    Long getId();

    String getTitle();

    Timestamp getLastMessageCreatedAt();

    Long getToUserId();

    String getLastMessageText();
}
