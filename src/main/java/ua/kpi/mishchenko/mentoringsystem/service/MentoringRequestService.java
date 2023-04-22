package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.dto.MentoringRequestDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MentoringRequestBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestFilter;

public interface MentoringRequestService {

    PageBO<MentoringRequestDTO> getMentoringRequests(MentoringRequestFilter filter, int numberOfPage);

    void createMentoringRequest(String fromEmail, MentoringRequestBO mentoringRequest);

    void acceptMentoringReqStatusById(Long reqId, String email);

    void rejectMentoringReqStatusById(Long reqId, String email);

    void cancelMentoringReqStatusById(Long reqId, String email);
}
