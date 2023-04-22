package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MentoringRequestDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.MentoringRequestEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.UserEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.MentoringRequestMapper;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MentoringRequestBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;
import ua.kpi.mishchenko.mentoringsystem.repository.MentoringRequestRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;
import ua.kpi.mishchenko.mentoringsystem.service.MentoringRequestService;

import java.util.List;

import static java.util.Objects.isNull;
import static org.springframework.data.jpa.domain.Specification.where;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ua.kpi.mishchenko.mentoringsystem.domain.entity.specification.MentoringRequestSpecification.matchFromEmail;
import static ua.kpi.mishchenko.mentoringsystem.domain.entity.specification.MentoringRequestSpecification.matchStatus;
import static ua.kpi.mishchenko.mentoringsystem.domain.entity.specification.MentoringRequestSpecification.matchToEmail;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.ACCEPTED;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.CANCELED;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.PENDING;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.REJECTED;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus.ACTIVE;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.getTimestampNow;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.lessThanOne;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentoringRequestServiceImpl implements MentoringRequestService {

    private static final int PAGE_SIZE = 5;

    private final MentoringRequestRepository mentoringRequestRepository;
    private final MentoringRequestMapper mentoringRequestMapper;
    private final UserRepository userRepository;

    @Override
    public PageBO<MentoringRequestDTO> getMentoringRequests(MentoringRequestFilter filter, int numberOfPage) {
        log.debug("Getting mentoring requests");
        if (lessThanOne(numberOfPage)) {
            log.warn("The number of page and size of page must be greater than zero");
            throw new ResponseStatusException(BAD_REQUEST, "Номер сторінки не може бути менше 1.");
        }
        Page<MentoringRequestEntity> mentoringReqPage = mentoringRequestRepository.findAll(
                where(matchStatus(filter.getStatus()))
                        .and(matchFromEmail(filter.getFromEmail()))
                        .and(matchToEmail(filter.getToEmail())),
                PageRequest.of(numberOfPage - 1, PAGE_SIZE));
        if (!mentoringReqPage.hasContent()) {
            log.debug("Cannot find mentoring requests with this filter parameters");
            return new PageBO<>(numberOfPage, mentoringReqPage.getTotalPages());
        }
        List<MentoringRequestDTO> mentoringReqDtos = mentoringReqPage.getContent()
                .stream()
                .map(mentoringRequestMapper::entityToDto)
                .toList();
        return new PageBO<>(mentoringReqDtos, numberOfPage, mentoringReqPage.getTotalPages());
    }

    @Override
    public void createMentoringRequest(String fromEmail, MentoringRequestBO mentoringRequest) {
        log.debug("Creating mentoring request");
        if (existsByFromEmailAndToIdAndPendingStatus(fromEmail, mentoringRequest.getToUserId())) {
            log.debug("The request from user with email = [{}] to user with id = [{}] already exists", fromEmail, mentoringRequest.getToUserId());
            throw new ResponseStatusException(BAD_REQUEST, "Запит уже надіслано, очікуйте відповіді.");
        }
        UserEntity userFrom = userRepository.findByEmail(fromEmail).orElse(null);
        if (isNull(userFrom)) {
            log.warn("Cannot find userFrom by email = [{}]", fromEmail);
            throw new ResponseStatusException(BAD_REQUEST, "Не вдається надіслати запит. Спробуйте ще раз пізніше.");
        }
        if (!ACTIVE.equals(userFrom.getStatus())) {
            log.debug("The userFrom has not ACTIVE status");
            throw new ResponseStatusException(BAD_REQUEST, "Необхідно заповнити профіль, щоб відправляти запити.");
        }
        UserEntity userTo = userRepository.findById(mentoringRequest.getToUserId()).orElse(null);
        if (isNull(userTo)) {
            log.warn("Cannot find userTo by id = [{}]", mentoringRequest.getToUserId());
            throw new ResponseStatusException(BAD_REQUEST, "Не вдається надіслати запит. Спробуйте ще раз пізніше.");
        }
        if (!ACTIVE.equals(userTo.getStatus())) {
            log.debug("The userTo has not ACTIVE status");
            throw new ResponseStatusException(BAD_REQUEST, "Користувач не заповнив профіль, тому йому не можна відправляти запити.");
        }
        if (userFrom.equals(userTo)) {
            log.warn("Cannot send request to self");
            throw new ResponseStatusException(BAD_REQUEST, "Не можливо надіслати запит самому собі.");
        }
        if (userFrom.getRole().getName().equals(userTo.getRole().getName())) {
            log.warn("Cannot send request to user with the same role");
            throw new ResponseStatusException(BAD_REQUEST, "Не можливо надіслати запит користувачу, який має тип профілю, як і Ваш.");
        }
        MentoringRequestEntity mentoringRequestEntity = createMentoringRequestEntity(userFrom, userTo);
        mentoringRequestRepository.save(mentoringRequestEntity);
    }

    private boolean existsByFromEmailAndToIdAndPendingStatus(String fromEmail, Long toUserId) {
        return mentoringRequestRepository.existsByFromEmailAndToIdAndStatus(fromEmail, toUserId, PENDING);
    }

    private MentoringRequestEntity createMentoringRequestEntity(UserEntity userFrom, UserEntity userTo) {
        MentoringRequestEntity entity = new MentoringRequestEntity();
        entity.setFrom(userFrom);
        entity.setTo(userTo);
        entity.setStatus(PENDING);
        entity.setCreatedAt(getTimestampNow());
        entity.setUpdatedAt(getTimestampNow());
        return entity;
    }

    @Override
    public void acceptMentoringReqStatusById(Long reqId, String email) {
        log.debug("Accepting mentoring request by id = [{}]", reqId);
        MentoringRequestEntity mentoringReqEntity = getMentoringRequestEntityById(reqId);
        final MentoringRequestStatus oldStatus = mentoringReqEntity.getStatus();
        final MentoringRequestStatus newStatus = ACCEPTED;
        if (isPending(oldStatus) && statusesNotEquals(oldStatus, newStatus)) {
            if (checkIfUserHasRights(email, mentoringReqEntity.getTo().getEmail())) {
                updateMentoringRequestStatus(mentoringReqEntity, newStatus);
                return;
            }
            logAndThrowForbiddenException("Схоже Ви не маєте прав, щоб прийняти цей запит.");
        }
        logAndThrowProcessedReqException();
    }

    @Override
    public void rejectMentoringReqStatusById(Long reqId, String email) {
        log.debug("Rejecting mentoring request by id = [{}]", reqId);
        MentoringRequestEntity mentoringReqEntity = getMentoringRequestEntityById(reqId);
        final MentoringRequestStatus oldStatus = mentoringReqEntity.getStatus();
        final MentoringRequestStatus newStatus = REJECTED;
        if (isPending(oldStatus) && statusesNotEquals(oldStatus, newStatus)) {
            if (checkIfUserHasRights(email, mentoringReqEntity.getTo().getEmail())) {
                updateMentoringRequestStatus(mentoringReqEntity, newStatus);
                return;
            }
            logAndThrowForbiddenException("Схоже Ви не маєте прав, щоб відхилити цей запит.");
        }
        logAndThrowProcessedReqException();
    }

    @Override
    public void cancelMentoringReqStatusById(Long reqId, String email) {
        log.debug("Canceling mentoring request by id = [{}]", reqId);
        MentoringRequestEntity mentoringReqEntity = getMentoringRequestEntityById(reqId);
        final MentoringRequestStatus oldStatus = mentoringReqEntity.getStatus();
        final MentoringRequestStatus newStatus = CANCELED;
        if (isPending(oldStatus) && statusesNotEquals(oldStatus, newStatus)) {
            if (checkIfUserHasRights(email, mentoringReqEntity.getFrom().getEmail())) {
                updateMentoringRequestStatus(mentoringReqEntity, newStatus);
                return;
            }
            logAndThrowForbiddenException("Схоже Ви не маєте прав, щоб відмінити цей запит.");
        }
        logAndThrowProcessedReqException();
    }

    private MentoringRequestEntity getMentoringRequestEntityById(Long reqId) {
        if (!existsById(reqId)) {
            log.warn("The mentoring request with id = [{}] does not exist", reqId);
            throw new ResponseStatusException(NOT_FOUND, "Не вдається знайти запит, схоже його не існує.");
        }
        return mentoringRequestRepository.findById(reqId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find mentoring request by id = [" + reqId + "]"));
    }

    private boolean existsById(Long reqId) {
        return mentoringRequestRepository.existsById(reqId);
    }

    private boolean isPending(MentoringRequestStatus oldStatus) {
        return PENDING.equals(oldStatus);
    }

    private boolean statusesNotEquals(MentoringRequestStatus status1, MentoringRequestStatus status2) {
        return !status1.equals(status2);
    }

    private boolean checkIfUserHasRights(String authEmail, String reqEmail) {
        return authEmail.equals(reqEmail);
    }

    private void updateMentoringRequestStatus(MentoringRequestEntity mentoringReqEntity, MentoringRequestStatus newStatus) {
        mentoringReqEntity.setStatus(newStatus);
        mentoringRequestRepository.save(mentoringReqEntity);
    }

    private void logAndThrowForbiddenException(String exMessage) {
        log.debug("The user has not right to change status");
        throw new ResponseStatusException(FORBIDDEN, exMessage);
    }

    private void logAndThrowProcessedReqException() {
        log.debug("The mentoring request can't change status from Accepted or Rejected");
        throw new ResponseStatusException(BAD_REQUEST, "Запит уже був опрацьований.");
    }
}
