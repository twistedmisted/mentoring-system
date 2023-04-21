package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.Builder;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;

@Data
@Builder
public class MentoringRequestResponse {

    private Long id;
    private UserWithPhoto from;
    private UserWithPhoto to;
    private MentoringRequestStatus status;
    private String createdAt;
    private String updatedAt;
}
