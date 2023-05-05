package ua.kpi.mishchenko.mentoringsystem.repository.projection;

import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;

public interface MentoringRequestProjection {

    Long getId();

    RequestUserProjection getFromUser();

    MentoringRequestStatus getStatus();
}
