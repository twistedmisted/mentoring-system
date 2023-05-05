package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.Builder;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;

@Data
@Builder
public class MentoringRequestPayload {

    private Long id;
    private RequestUser from;
    private MentoringRequestStatus status;
}
