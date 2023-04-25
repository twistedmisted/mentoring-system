package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.Builder;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;

@Data
@Builder
public class MentoringRequestResponse {

    private Long id;
    private UserWithQuestionnaire from;
    private UserWithQuestionnaire to;
    private MentoringRequestStatus status;
    private String createdAt;
    private String updatedAt;
}
