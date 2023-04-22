package ua.kpi.mishchenko.mentoringsystem.facade.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MediaDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MentoringRequestDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.UserMapper;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MentoringRequestBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MentoringRequestResponse;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPassword;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPhoto;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.PhotoExtension;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;
import ua.kpi.mishchenko.mentoringsystem.exception.IllegalPhotoExtensionException;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.service.MentoringRequestService;
import ua.kpi.mishchenko.mentoringsystem.service.S3Service;
import ua.kpi.mishchenko.mentoringsystem.service.UserService;

import java.io.IOException;

import static java.util.Objects.isNull;
import static ua.kpi.mishchenko.mentoringsystem.service.impl.S3ServiceImpl.PROFILE_PHOTO;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.parseTimestampToStringDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentoringSystemFacadeImpl implements MentoringSystemFacade {

    private final UserService userService;
    private final MentoringRequestService mentoringRequestService;
    private final S3Service s3Service;
    private final UserMapper userMapper;

    @Override
    public UserWithPhoto getUserWithPhotoById(Long userId) {
        log.debug("Getting user with photo by id = [{}]", userId);
        UserDTO userDTO = userService.getUserById(userId);
        String profilePhotoUrl = getProfilePhotoUrlByUserId(userId);
        return createUserWithPhoto(userDTO, profilePhotoUrl);
    }

    @Override
    public UserWithPhoto getUserByEmail(String email) {
        log.debug("Getting user with photo by email = [{}]", email);
        UserDTO userDTO = userService.getUserByEmail(email);
        String profilePhotoUrl = getProfilePhotoUrlByUserId(userDTO.getId());
        return createUserWithPhoto(userDTO, profilePhotoUrl);
    }

    private UserWithPhoto createUserWithPhoto(UserDTO userDTO, String profilePhotoUrl) {
        return UserWithPhoto.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .email(userDTO.getEmail())
                .role(userDTO.getRole())
                .status(userDTO.getStatus())
                .createdAt(parseTimestampToStringDate(userDTO.getCreatedAt()))
                .questionnaire(userDTO.getQuestionnaire())
                .profilePhotoUrl(profilePhotoUrl)
                .build();
    }

    private String getProfilePhotoUrlByUserId(Long userId) {
        return s3Service.getUserPhoto(userId);
    }

    @Override
    public void updateUserById(Long userId, UserWithPassword user, MultipartFile photo) {
        if (!isNull(photo)) {
            s3Service.uploadUserPhoto(userId, parseToMediaDTO(photo));
        }
        if (!isNull(user)) {
            userService.updateUserById(userId, userMapper.userWithPasswordToDto(user));
        }
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
    public boolean checkIfIdAndEmailMatch(Long id, String email) {
        return userService.existsByIdAndEmail(id, email);
    }

    @Override
    public PageBO<UserWithPhoto> getUsers(UserFilter userFilter, int numberOfPage) {
        PageBO<UserDTO> userPage = userService.getUsers(userFilter, numberOfPage);
        PageBO<UserWithPhoto> userWithPhotoPage = new PageBO<>(userPage.getCurrentPageNumber(), userPage.getTotalPages());
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
}
