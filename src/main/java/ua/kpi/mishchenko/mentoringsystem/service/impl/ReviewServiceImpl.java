package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MentoringRequestDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.ReviewDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.ReviewMapper;
import ua.kpi.mishchenko.mentoringsystem.entity.ReviewEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.MentoringRequestRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.ReviewRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.MentoringReqIdAndExistsReview;
import ua.kpi.mishchenko.mentoringsystem.service.ReviewService;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.FINISHED;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private static final int PAGE_SIZE = 5;

    private final ReviewRepository reviewRepository;
    private final MentoringRequestRepository mentoringRequestRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public PageBO<ReviewDTO> getReviewsByUserId(Long userId, int numberOfPage) {
        log.debug("Getting reviews by user id = [{}] on page = [{}]", userId, numberOfPage);
        Page<ReviewEntity> reviewPage = reviewRepository.findAllByToUserId(userId,
                PageRequest.of(numberOfPage - 1, PAGE_SIZE, Sort.by(DESC, "createdAt")));
        if (!reviewPage.hasContent()) {
            log.debug("Cannot find reviews for user by id = [{}] on page = [{}]", userId, numberOfPage);
            return new PageBO<>();
        }
        List<ReviewDTO> reviewDtos = reviewPage.getContent()
                .stream()
                .map(reviewMapper::entityToDto)
                .toList();
        return new PageBO<>(reviewDtos, numberOfPage, reviewPage.getTotalPages());
    }

    @Override
    public void createReview(ReviewDTO reviewDto) {
        log.debug("Creating new review");
        Long toUserId = reviewDto.getToUser().getId();
        Long fromUserId = reviewDto.getFromUser().getId();
        if (toUserId.equals(fromUserId)) {
            log.debug("Cannot write comment to self");
            throw new ResponseStatusException(BAD_REQUEST, "Ви не можете писати відгук самому собі.");
        }
        if (!existsMentoringReqByUsersAndFinishedStatus(fromUserId, toUserId)) {
            log.debug("Cannot create review because users do not have connections");
            throw new ResponseStatusException(BAD_REQUEST,
                    "Не можливо написати відгук користувачу, " +
                            "оскільки Ви не мали з ним жодних завершених співпраць.");
        }
        MentoringReqIdAndExistsReview lastFinishedMentoringReq =
                mentoringRequestRepository.findLastFinishedMentoringReqIdAndExistsReview(fromUserId, toUserId);
        if (!lastFinishedMentoringReq.getExistsReview()) {
            log.debug("Cannot create review for the last mentoring request because it already exists");
            throw new ResponseStatusException(BAD_REQUEST, "Ви можете писати лише 1 коментарій до останньої співпраці з цїєю людиною.");
        }
        reviewDto.setMentoringRequest(MentoringRequestDTO.builder().id(lastFinishedMentoringReq.getId()).build());
        reviewRepository.save(reviewMapper.dtoToEntity(reviewDto));
    }

    @Override
    public boolean checkIfUserCanWriteReview(String fromUserEmail, Long toUserId) {
        if (!existsMentoringReqByUsersAndFinishedStatus(fromUserEmail, toUserId)) {
            return false;
        }
        return reviewRepository.checkIfUserCanWriteReview(fromUserEmail, toUserId);
    }

    private boolean existsMentoringReqByUsersAndFinishedStatus(Long fromUserId, Long toUserId) {
        return mentoringRequestRepository.existsByTwoUsersAndStatus(fromUserId, toUserId, FINISHED);
    }

    private boolean existsMentoringReqByUsersAndFinishedStatus(String fromUserEmail, Long toUserId) {
        return mentoringRequestRepository.existsByTwoUsersAndStatus(toUserId, fromUserEmail, FINISHED);
    }

    @Override
    public double getAvgRatingByUserId(Long userId) {
        log.debug("Getting avg rating by user id = [{}]", userId);
        if (!existsByToUserId(userId)) {
            log.debug("Cannot find any reviews for user with id = [{}]", userId);
            return 0;
        }
        return reviewRepository.calculateAvgRatingByToUserId(userId);
    }

    private boolean existsByToUserId(Long userId) {
        return reviewRepository.existsByToUserId(userId);
    }
}
