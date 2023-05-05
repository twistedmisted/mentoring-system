package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.bo.MentoringRequestBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MentoringRequestDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestFilter;

public interface MentoringRequestService {

    MentoringRequestDTO getMentoringRequestByUsers(Long firstUserId, String secondUserEmail);

    PageBO<MentoringRequestDTO> getMentoringRequests(MentoringRequestFilter filter, int numberOfPage);

    MentoringRequestDTO createMentoringRequest(String fromEmail, MentoringRequestBO mentoringRequest);

    MentoringRequestDTO acceptMentoringReqStatusById(Long reqId, String email);

    MentoringRequestDTO rejectMentoringReqStatusById(Long reqId, String email);

    MentoringRequestDTO cancelMentoringReqStatusById(Long reqId, String email);

    MentoringRequestDTO finishMentoringReqStatusById(Long reqId, String email);

    boolean checkIfUserHasPendingReqs(String userEmail);
}
