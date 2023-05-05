package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationPayload {

    private String text;
}
