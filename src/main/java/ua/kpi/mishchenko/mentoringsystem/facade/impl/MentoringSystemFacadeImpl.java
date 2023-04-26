package ua.kpi.mishchenko.mentoringsystem.facade.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.MentoringRequestBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.QuestionnaireBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.ReviewBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MediaDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MentoringRequestDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.QuestionnaireDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.ReviewDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.CreateReviewRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MentoringRequestResponse;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.QuestionnaireUpdateRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UpdatePasswordRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithQuestionnaire;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.PhotoExtension;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;
import ua.kpi.mishchenko.mentoringsystem.exception.IllegalPhotoExtensionException;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.service.MentoringRequestService;
import ua.kpi.mishchenko.mentoringsystem.service.QuestionnaireService;
import ua.kpi.mishchenko.mentoringsystem.service.ReviewService;
import ua.kpi.mishchenko.mentoringsystem.service.S3Service;
import ua.kpi.mishchenko.mentoringsystem.service.UserService;

import java.io.IOException;

import static java.util.Objects.isNull;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus.ACTIVE;
import static ua.kpi.mishchenko.mentoringsystem.service.impl.S3ServiceImpl.PROFILE_PHOTO;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.getTimestampNow;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.parseTimestampToStringDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentoringSystemFacadeImpl implements MentoringSystemFacade {

    private final UserService userService;
    private final MentoringRequestService mentoringRequestService;
    private final S3Service s3Service;
    private final ReviewService reviewService;
    private final QuestionnaireService questionnaireService;

    @Override
    public UserWithQuestionnaire getUserWithPhotoById(Long userId) {
        log.debug("Getting user with photo by id = [{}]", userId);
        UserDTO userDTO = userService.getUserById(userId);
        String profilePhotoUrl = getProfilePhotoUrlByUserId(userId);
        return createUserWithPhoto(userDTO, profilePhotoUrl);
    }

    @Override
    public UserWithQuestionnaire getUserByEmail(String email) {
        log.debug("Getting user with photo by email = [{}]", email);
        UserDTO userDTO = userService.getUserByEmail(email);
        String profilePhotoUrl = getProfilePhotoUrlByUserId(userDTO.getId());
        return createUserWithPhoto(userDTO, profilePhotoUrl);
    }

    private UserWithQuestionnaire createUserWithPhoto(UserDTO userDTO, String profilePhotoUrl) {
        return UserWithQuestionnaire.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .role(userDTO.getRole())
                .status(userDTO.getStatus())
                .createdAt(parseTimestampToStringDate(userDTO.getCreatedAt()))
                .rating(reviewService.getAvgRatingByUserId(userDTO.getId()))
                .questionnaire(createQuestionnaireBO(userDTO.getQuestionnaire(), profilePhotoUrl))
                .build();
    }

    private QuestionnaireBO createQuestionnaireBO(QuestionnaireDTO questionnaire, String profilePhotoUrl) {
        if (isNull(questionnaire)) {
            return new QuestionnaireBO();
        }
        QuestionnaireBO questionnaireBO = new QuestionnaireBO();
        questionnaireBO.setRank(questionnaire.getRank());
        questionnaireBO.setCompanies(questionnaire.getCompanies());
        questionnaireBO.setSkills(questionnaire.getSkills());
        questionnaireBO.setLinkedin(questionnaire.getLinkedin());
        questionnaireBO.setSpecialization(questionnaire.getSpecialization());
        questionnaireBO.setAbout(questionnaire.getAbout());
        questionnaireBO.setHoursPerWeek(questionnaire.getHoursPerWeek());
        questionnaireBO.setProfilePhotoUrl(profilePhotoUrl);
        return questionnaireBO;
    }

    private String getProfilePhotoUrlByUserId(Long userId) {
        return s3Service.getUserPhoto(userId);
    }

    private MediaDTO parseToMediaDTO(MultipartFile photo) {
        String originalFilename = photo.getOriginalFilename();
        if (!checkPhotoFileExtension(photo.getContentType())) {
            throw new IllegalPhotoExtensionException("Розширення '" + originalFilename + "' не дозволене. Фото має бути .jpg, .jpeg, .png.");
        }
        MediaDTO media = new MediaDTO();
        media.setFilename(PROFILE_PHOTO + originalFilename.substring(originalFilename.indexOf('.')));
        try {
            media.setInputStream(photo.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Cannot get input stream from photo");
        }
        return media;
    }

    private boolean checkPhotoFileExtension(String contentType) {
        return PhotoExtension.exists(contentType);
    }

    @Override
    public PageBO<UserWithQuestionnaire> getUsers(UserFilter userFilter, int numberOfPage) {
        PageBO<UserDTO> userPage = userService.getUsers(userFilter, numberOfPage);
        PageBO<UserWithQuestionnaire> userWithPhotoPage = new PageBO<>(userPage.getCurrentPageNumber(), userPage.getTotalPages());
        for (UserDTO userDTO : userPage.getContent()) {
            String profilePhotoUrl = getProfilePhotoUrlByUserId(userDTO.getId());
            userWithPhotoPage.addElement(createUserWithPhoto(userDTO, profilePhotoUrl));
        }
        return userWithPhotoPage;
    }

    @Override
    public PageBO<MentoringRequestResponse> getMentoringRequests(MentoringRequestFilter filter, int numberOfPage) {
        PageBO<MentoringRequestDTO> mentoringReqPage = mentoringRequestService.getMentoringRequests(filter, numberOfPage);
        PageBO<MentoringRequestResponse> mentoringReqResult = new PageBO<>(mentoringReqPage.getCurrentPageNumber(),
                mentoringReqPage.getTotalPages());
        for (MentoringRequestDTO mentoringReqDto : mentoringReqPage.getContent()) {
            String fromPhotoUrl = getProfilePhotoUrlByUserId(mentoringReqDto.getFrom().getId());
            String toPhotoUrl = getProfilePhotoUrlByUserId(mentoringReqDto.getTo().getId());
            mentoringReqResult.addElement(createMentoringReqResponse(mentoringReqDto, fromPhotoUrl, toPhotoUrl));
        }
        return mentoringReqResult;
    }

    private MentoringRequestResponse createMentoringReqResponse(MentoringRequestDTO mentoringReqDto, String fromPhotoUrl, String toPhotoUrl) {
        return MentoringRequestResponse.builder()
                .id(mentoringReqDto.getId())
                .from(createUserWithPhoto(mentoringReqDto.getFrom(), fromPhotoUrl))
                .to(createUserWithPhoto(mentoringReqDto.getTo(), toPhotoUrl))
                .status(mentoringReqDto.getStatus())
                .createdAt(parseTimestampToStringDate(mentoringReqDto.getCreatedAt()))
                .updatedAt(parseTimestampToStringDate(mentoringReqDto.getUpdatedAt()))
                .build();
    }

    @Override
    @Transactional
    public void createMentoringRequest(String fromEmail, MentoringRequestBO mentoringRequest) {
        log.debug("Creating new mentoring request");
        mentoringRequestService.createMentoringRequest(fromEmail, mentoringRequest);
        // TODO: send push notification
    }

    @Override
    public void acceptMentoringReq(Long reqId, String email) {
        log.debug("Accepting mentoring request by id = [{}]", reqId);
        mentoringRequestService.acceptMentoringReqStatusById(reqId, email);
    }

    @Override
    public void rejectMentoringReq(Long reqId, String email) {
        log.debug("Rejecting mentoring request by id = [{}]", reqId);
        mentoringRequestService.rejectMentoringReqStatusById(reqId, email);
    }

    @Override
    public void cancelMentoringReq(Long reqId, String email) {
        log.debug("Canceling mentoring request by id = [{}]", reqId);
        mentoringRequestService.cancelMentoringReqStatusById(reqId, email);
    }

    @Override
    public PageBO<ReviewBO> getReviewsByUserId(Long userId, int numberOfPage) {
        log.debug("Getting reviews by user id = [{}]", userId);
        PageBO<ReviewDTO> reviewPage = reviewService.getReviewsByUserId(userId, numberOfPage);
        PageBO<ReviewBO> reviewPageResult = new PageBO<>(reviewPage.getCurrentPageNumber(), reviewPage.getTotalPages());
        for (ReviewDTO review : reviewPage.getContent()) {
            ReviewBO reviewBO = new ReviewBO();
            reviewBO.setUserId(review.getFromUser().getId());
            reviewBO.setName(review.getFromUser().getName());
            reviewBO.setSurname(review.getFromUser().getSurname());
            reviewBO.setText(review.getText());
            reviewBO.setRating(review.getRating());
            reviewBO.setCreatedAt(parseTimestampToStringDate(review.getCreatedAt()));
            reviewPageResult.addElement(reviewBO);
        }
        return reviewPageResult;
    }

    @Override
    public void createReview(CreateReviewRequest review, String fromEmail) {
        log.debug("Creating new review for user with id = [{}] from = [{}]",
                review.getToUserId(), fromEmail);
        reviewService.createReview(createReviewDto(review, fromEmail));
    }

    private ReviewDTO createReviewDto(CreateReviewRequest review, String fromEmail) {
        UserDTO userTo = new UserDTO();
        userTo.setId(review.getToUserId());
        UserDTO userFrom = userService.getUserByEmail(fromEmail);
        ReviewDTO reviewDto = new ReviewDTO();
        reviewDto.setToUser(userTo);
        reviewDto.setFromUser(userFrom);
        reviewDto.setText(review.getText());
        reviewDto.setRating(review.getRating());
        reviewDto.setCreatedAt(getTimestampNow());
        return reviewDto;
    }

    @Override
    @Transactional
    public UserWithQuestionnaire updateQuestionnaireByUserEmail(String email, QuestionnaireUpdateRequest questionnaire, MultipartFile photo) {
        log.debug("Updating questionnaire by user email = [{}]", email);
        Long userId = userService.getUserByEmail(email).getId();
        if (!isNull(photo)) {
            s3Service.uploadUserPhoto(userId, parseToMediaDTO(photo));
        }
        if (!isNull(questionnaire)) {
            questionnaireService.updateQuestionnaire(createQuestionnaireDto(questionnaire, userId));
            userService.updateUserStatusById(userId, ACTIVE);
        }
        return getUserWithPhotoById(userId);
    }

    private QuestionnaireDTO createQuestionnaireDto(QuestionnaireUpdateRequest questionnaire, Long userId) {
        QuestionnaireDTO dto = new QuestionnaireDTO();
        dto.setUserId(userId);
        dto.setAbout(questionnaire.getAbout());
        dto.setLinkedin(questionnaire.getLinkedin());
        dto.setCompanies(questionnaire.getCompanies());
        dto.setSkills(questionnaire.getSkills());
        dto.setSpecialization(questionnaire.getSpecialization());
        dto.setRank(questionnaire.getRank());
        dto.setHoursPerWeek(questionnaire.getHoursPerWeek());
        return dto;
    }

    @Override
    public void updateUserPasswordByEmail(String email, UpdatePasswordRequest passwordRequest) {
        log.debug("Updating user password by email = [{}]", email);
        userService.updateUserPasswordByEmail(email, passwordRequest);
    }

    @Override
    public void deleteProfilePhotoByUserEmail(String email) {
        UserDTO user = userService.getUserByEmail(email);
        s3Service.removeUserPhoto(user.getId());
    }
}
