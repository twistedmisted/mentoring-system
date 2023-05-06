package ua.kpi.mishchenko.mentoringsystem.facade.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.MentoringRequestBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.QuestionnaireBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.ReviewBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.ChatDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MediaDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MentoringRequestDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.QuestionnaireDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.ReviewDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.CreateReviewRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MentoringRequestPayload;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.QuestionnaireUpdateRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.RequestUser;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UpdatePasswordRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithQuestionnaire;
import ua.kpi.mishchenko.mentoringsystem.domain.util.ChatStatus;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.PhotoExtension;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;
import ua.kpi.mishchenko.mentoringsystem.exception.IllegalPhotoExtensionException;
import ua.kpi.mishchenko.mentoringsystem.facade.ChatSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.facade.NotificationSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.service.ChatService;
import ua.kpi.mishchenko.mentoringsystem.service.MentoringRequestService;
import ua.kpi.mishchenko.mentoringsystem.service.QuestionnaireService;
import ua.kpi.mishchenko.mentoringsystem.service.ReviewService;
import ua.kpi.mishchenko.mentoringsystem.service.S3Service;
import ua.kpi.mishchenko.mentoringsystem.service.UserService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus.ACTIVE;
import static ua.kpi.mishchenko.mentoringsystem.service.impl.S3ServiceImpl.PROFILE_PHOTO;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.getTimestampNow;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.parseTimestampToStringDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentoringSystemFacadeImpl implements MentoringSystemFacade {

    private static final String QUEUE_MENTORING_REQ_DESTINATION = "/queue/mentoring-requests";

    private final UserService userService;
    private final MentoringRequestService mentoringRequestService;
    private final S3Service s3Service;
    private final ReviewService reviewService;
    private final QuestionnaireService questionnaireService;
    private final ChatService chatService;
    private final ChatSystemFacade chatSystemFacade;

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SimpUserRegistry simpUserRegistry;

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
    public PageBO<MentoringRequestPayload> getMentoringRequests(MentoringRequestFilter filter, int numberOfPage) {
        PageBO<MentoringRequestDTO> mentoringReqPage = mentoringRequestService.getMentoringRequests(filter, numberOfPage);
        PageBO<MentoringRequestPayload> mentoringReqResult = new PageBO<>(mentoringReqPage.getCurrentPageNumber(),
                mentoringReqPage.getTotalPages());
        for (MentoringRequestDTO mentoringReqDto : mentoringReqPage.getContent()) {
            mentoringReqResult.addElement(createMentoringReqPayload(mentoringReqDto));
        }
        return mentoringReqResult;
    }

    private final NotificationSystemFacade notificationSystemFacade;

    @Override
    @Transactional
    public MentoringRequestPayload createMentoringRequest(String fromEmail, MentoringRequestBO mentoringRequest) {
        log.debug("Creating new mentoring request");
        MentoringRequestDTO mentoringRequestDTO = mentoringRequestService.createMentoringRequest(fromEmail, mentoringRequest);
        MentoringRequestPayload mentoringReqPayload = createMentoringReqPayload(mentoringRequestDTO);
        sendMentoringReqUpdate(mentoringReqPayload, Arrays.asList(mentoringRequestDTO.getTo().getEmail(),
                mentoringRequestDTO.getFrom().getEmail()));
        notificationSystemFacade.sendNotificationByUserEmail(mentoringRequestDTO.getTo().getEmail(), "Ви отримали новий запит на менторство.");
        return mentoringReqPayload;
    }

    private void sendMentoringReqUpdate(MentoringRequestPayload mentoringReqPayload, List<String> mentoringReqUsersEmails) {
        Set<SimpSubscription> chatsSubs = findSubsByDestinationEndWith(QUEUE_MENTORING_REQ_DESTINATION);
        chatsSubs.stream()
                .filter(s -> mentoringReqUsersEmails.contains(s.getSession().getUser().getName()))
                .forEach(s -> createAndSendMentoringReqUpdate(s.getSession(), mentoringReqPayload));
    }

    private Set<SimpSubscription> findSubsByDestinationEndWith(String endWith) {
        return simpUserRegistry.findSubscriptions(s -> s.getDestination().endsWith(endWith));
    }

    private void createAndSendMentoringReqUpdate(SimpSession session, MentoringRequestPayload mentoringReqPayload) {
        sendToUserBySessionId(session.getId(), QUEUE_MENTORING_REQ_DESTINATION, mentoringReqPayload);
    }

    private void sendToUserBySessionId(String sessionId, String destination, Object payload) {
        simpMessagingTemplate.convertAndSendToUser(sessionId, destination, payload, createHeaders(sessionId));
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    @Override
    @Transactional
    public MentoringRequestPayload acceptMentoringReq(Long reqId, String email) {
        log.debug("Accepting mentoring request by id = [{}]", reqId);
        MentoringRequestDTO mentoringRequestDTO = mentoringRequestService.acceptMentoringReqStatusById(reqId, email);
        ChatDTO chat = chatService.createChat(createChatForMentoringReq(mentoringRequestDTO));
        if (!isNull(chat)) {
            chatSystemFacade.addNewChatToPageIfSubscribed(chat.getId(), chat.getUsers().stream().map(UserDTO::getEmail).toList());
        }
        return createMentoringReqPayload(mentoringRequestDTO);
    }

    private ChatDTO createChatForMentoringReq(MentoringRequestDTO mentoringRequestDTO) {
        ChatDTO chat = new ChatDTO();
        chat.addMentoringReqId(mentoringRequestDTO.getId());
        chat.setStatus(ChatStatus.ACTIVE);
        chat.setUsers(getUsersFromMentoringRequest(mentoringRequestDTO));
        chat.setCreatedAt(getTimestampNow());
        return chat;
    }

    private Set<UserDTO> getUsersFromMentoringRequest(MentoringRequestDTO mentoringRequestDTO) {
        Set<UserDTO> users = new HashSet<>();
        users.add(mentoringRequestDTO.getFrom());
        users.add(mentoringRequestDTO.getTo());
        return users;
    }

    @Override
    public MentoringRequestPayload rejectMentoringReq(Long reqId, String email) {
        log.debug("Rejecting mentoring request by id = [{}]", reqId);
        MentoringRequestDTO mentoringRequestDTO = mentoringRequestService.rejectMentoringReqStatusById(reqId, email);
        return createMentoringReqPayload(mentoringRequestDTO);
    }

    @Override
    public MentoringRequestPayload cancelMentoringReq(Long reqId, String email) {
        log.debug("Canceling mentoring request by id = [{}]", reqId);
        MentoringRequestDTO mentoringRequestDTO = mentoringRequestService.cancelMentoringReqStatusById(reqId, email);
        return createMentoringReqPayload(mentoringRequestDTO);
    }

    @Override
    @Transactional
    public MentoringRequestPayload finishMentoringReq(Long reqId, String email) {
        log.debug("Finishing mentoring request by id = [{}]", reqId);
        MentoringRequestDTO mentoringRequestDTO = mentoringRequestService.finishMentoringReqStatusById(reqId, email);
        chatService.archiveChatByMentoringReqId(mentoringRequestDTO.getId());
        return createMentoringReqPayload(mentoringRequestDTO);
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
        ReviewDTO reviewDto = new ReviewDTO();
        reviewDto.setToUser(UserDTO.builder().id(review.getToUserId()).build());
        reviewDto.setFromUser(UserDTO.builder().id(userService.getUserIdByEmail(fromEmail)).build());
        reviewDto.setText(review.getText());
        reviewDto.setRating(review.getRating());
        reviewDto.setCreatedAt(getTimestampNow());
        return reviewDto;
    }

    @Override
    @Transactional
    public void updateQuestionnaireByUserEmail(String email,
                                               QuestionnaireUpdateRequest questionnaire,
                                               MultipartFile photo) {
        log.debug("Updating questionnaire by user email = [{}]", email);
        Long userId = userService.getUserByEmail(email).getId();
        if (!isNull(photo)) {
            s3Service.uploadUserPhoto(userId, parseToMediaDTO(photo));
        }
        if (!isNull(questionnaire)) {
            questionnaireService.updateQuestionnaire(createQuestionnaireDto(questionnaire, userId));
            userService.updateUserStatusById(userId, ACTIVE);
        }
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
    public void deleteProfilePhotoByUserEmail(String userEmail) {
        UserDTO user = userService.getUserByEmail(userEmail);
        s3Service.removeUserPhoto(user.getId());
    }

    @Override
    public MentoringRequestPayload getLastMentoringRequestByUsers(Long firstUserId, String secondUserEmail) {
        log.debug("Get last mentoring request by users");
        MentoringRequestDTO mentoringRequestDTO = mentoringRequestService.getMentoringRequestByUsers(firstUserId, secondUserEmail);
        return createMentoringReqPayload(mentoringRequestDTO);
    }

    private MentoringRequestPayload createMentoringReqPayload(MentoringRequestDTO mentoringRequestDTO) {
        if (isNull(mentoringRequestDTO)) {
            return null;
        }
        UserDTO from = mentoringRequestDTO.getFrom();
        return MentoringRequestPayload.builder()
                .id(mentoringRequestDTO.getId())
                .from(RequestUser.builder()
                        .id(from.getId())
                        .name(from.getName())
                        .surname(from.getSurname())
                        .profilePhotoUrl(getProfilePhotoUrlByUserId(from.getId()))
                        .build())
                .status(mentoringRequestDTO.getStatus())
                .build();
    }

    @Override
    public boolean checkIfUserCanWriteReview(String fromUserEmail, Long toUserId) {
        log.debug("Checking if user can write review");
        return reviewService.checkIfUserCanWriteReview(fromUserEmail, toUserId);
    }
}
