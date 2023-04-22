package ua.kpi.mishchenko.mentoringsystem.facade;

import org.springframework.web.multipart.MultipartFile;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MentoringRequestBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MentoringRequestResponse;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPassword;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPhoto;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;

public interface MentoringSystemFacade {

    UserDTO getUserById(Long userId);

    UserWithPhoto getUserWithPhotoById(Long userId);

    void updateUserById(Long userId, UserWithPassword user, MultipartFile photo);

    boolean checkIfIdAndEmailMatch(Long userId, String name);

    PageBO<UserWithPhoto> getUsers(UserFilter build, int numberOfPage);

    PageBO<MentoringRequestResponse> getMentoringRequests(MentoringRequestFilter filter, int numberOfPage);

    void createMentoringRequest(String fromEmail, MentoringRequestBO mentoringRequest);

    void acceptMentoringReq(Long reqId, String email);

    void rejectMentoringReq(Long reqId, String email);

    void cancelMentoringReq(Long reqId, String email);
}
