package ua.kpi.mishchenko.mentoringsystem.domain.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MentoringRequestFilter {

    private String fromEmail;
    private String toEmail;
    private MentoringRequestStatus status;
}
