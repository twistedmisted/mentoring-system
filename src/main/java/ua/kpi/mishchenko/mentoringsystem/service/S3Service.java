package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.dto.MediaDTO;

public interface S3Service {

    void uploadUserPhoto(Long userId, MediaDTO userPhoto);

    String getUserPhoto(Long userId);
}
