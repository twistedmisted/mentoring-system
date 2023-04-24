package ua.kpi.mishchenko.mentoringsystem.facade;

import org.springframework.web.multipart.MultipartFile;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.MentoringRequestBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.ReviewBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.CreateReviewRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MentoringRequestResponse;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPassword;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPhoto;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;

public interface MentoringSystemFacade {

    UserWithPhoto getUserWithPhotoById(Long userId);

    UserWithPhoto getUserByEmail(String email);

    void updateUserById(Long userId, UserWithPassword user, MultipartFile photo);

    boolean checkIfIdAndEmailMatch(Long userId, String name);

    PageBO<UserWithPhoto> getUsers(UserFilter build, int numberOfPage);

    PageBO<MentoringRequestResponse> getMentoringRequests(MentoringRequestFilter filter, int numberOfPage);

    void createMentoringRequest(String fromEmail, MentoringRequestBO mentoringRequest);

    void acceptMentoringReq(Long reqId, String email);

    void rejectMentoringReq(Long reqId, String email);

    void cancelMentoringReq(Long reqId, String email);

    PageBO<ReviewBO> getReviewsByUserId(Long userId, int numberOfPage);

    void createReview(CreateReviewRequest review, String fromEmail);
}
