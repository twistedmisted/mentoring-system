package ua.kpi.mishchenko.mentoringsystem.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MediaDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.UserMapper;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPassword;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.UserWithPhoto;
import ua.kpi.mishchenko.mentoringsystem.domain.util.PhotoExtension;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserFilter;
import ua.kpi.mishchenko.mentoringsystem.exception.IllegalPhotoExtensionException;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.service.S3Service;
import ua.kpi.mishchenko.mentoringsystem.service.UserService;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static java.util.Objects.isNull;
import static ua.kpi.mishchenko.mentoringsystem.service.impl.S3ServiceImpl.PROFILE_PHOTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentoringSystemFacadeImpl implements MentoringSystemFacade {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private final UserService userService;
    private final S3Service s3Service;
    private final UserMapper userMapper;

    @Override
    public UserDTO getUserById(Long userId) {
        log.debug("Getting user by id = [{}]", userId);
        return userService.getUserById(userId);
    }

    @Override
    public UserWithPhoto getUserWithPhotoById(Long userId) {
        log.debug("Getting user with photo by id = [{}]", userId);
        UserDTO userDTO = userService.getUserById(userId);
        String profilePhotoUrl = getProfilePhotoUrlByUserId(userId);
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
                .createdAt(DATE_FORMAT.format(userDTO.getCreatedAt()))
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
}
