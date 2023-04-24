package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.ReviewDTO;

public interface ReviewService {

    PageBO<ReviewDTO> getReviewsByUserId(Long userId, int numberOfPage);

    void createReview(ReviewDTO reviewDto);

    double getAvgRatingByUserId(Long userId);
}
