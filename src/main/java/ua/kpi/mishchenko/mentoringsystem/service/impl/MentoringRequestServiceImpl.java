package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.MentoringRequestBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MentoringRequestDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.MentoringRequestMapper;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestFilter;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;
import ua.kpi.mishchenko.mentoringsystem.entity.MentoringRequestEntity;
import ua.kpi.mishchenko.mentoringsystem.entity.UserEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.MentoringRequestRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;
import ua.kpi.mishchenko.mentoringsystem.service.MentoringRequestService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Objects.isNull;
import static org.springframework.data.jpa.domain.Specification.where;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.ACCEPTED;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.CANCELED;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.FINISHED;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.PENDING;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.REJECTED;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus.ACTIVE;
import static ua.kpi.mishchenko.mentoringsystem.entity.specification.MentoringRequestSpecification.matchFromEmail;
import static ua.kpi.mishchenko.mentoringsystem.entity.specification.MentoringRequestSpecification.matchStatus;
import static ua.kpi.mishchenko.mentoringsystem.entity.specification.MentoringRequestSpecification.matchToEmail;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.getTimestampNow;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.lessThanOne;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentoringRequestServiceImpl implements MentoringRequestService {

    private static final String NO_RIGHTS_TO_ACCEPT = "Схоже Ви не маєте прав, щоб прийняти цей запит.";
    private static final String NO_RIGHTS_TO_REJECT = "Схоже Ви не маєте прав, щоб відхилити цей запит.";
    private static final String NO_RIGHTS_TO_CANCEL = "Схоже Ви не маєте прав, щоб відмінити цей запит.";
    private static final String NO_RIGHTS_TO_FINISH = "Схоже Ви не маєте прав, щоб завершити співпрацю.";
    private static final int NUMBER_AVAILABLE_ACCEPTED_REQUESTS = 2;
    private static final int NUMBER_AVAILABLE_PENDING_REQUESTS = 5;
    private static final int PAGE_SIZE = 5;

    private final MentoringRequestRepository mentoringRequestRepository;
    private final MentoringRequestMapper mentoringRequestMapper;
    private final UserRepository userRepository;

    @Override
    public MentoringRequestDTO getMentoringRequestByUsers(Long firstUserId, String secondUserEmail) {
        log.debug("Getting mentoring request by users");
        return mentoringRequestMapper.projectionToDto(
                mentoringRequestRepository.findLastRequestByUsers(firstUserId, secondUserEmail)
                        .orElse(null));
    }

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
    public MentoringRequestDTO createMentoringRequest(String fromEmail, MentoringRequestBO mentoringRequest) {
        log.debug("Creating mentoring request");
        Long toUserId = mentoringRequest.getToUserId();
        if (userHasMoreThanAvailablePendingRequests(fromEmail)) {
            log.debug("Sender = [{}] can't send more than number of available requests", fromEmail);
            throw new ResponseStatusException(BAD_REQUEST, "Ви перевищили кількість відправлених повідомлень на день. Спробуйте завтра.");
        }
        if (userHasMoreThanAvailableAcceptedRequests(getCountByUserEmailAndStatus(fromEmail))) {
            log.debug("Sender = [{}] can't send more than number of available requests", fromEmail);
            throw new ResponseStatusException(BAD_REQUEST, "Ви перевищили кількість відкритих співпраць. Вам доступно лише 2 співпраці, щоб надіслати ще, необхідно завершити повередні.");
        }
        if (usersAlreadyHasRequestWithStatus(fromEmail, toUserId, PENDING)) {
            log.debug("The request for user with email = [{}] and user with id = [{}] with status PENDING already exists", fromEmail, toUserId);
            throw new ResponseStatusException(BAD_REQUEST, "Запит уже надіслано, очікуйте відповіді.");
        }
        if (usersAlreadyHasRequestWithStatus(fromEmail, toUserId, ACCEPTED)) {
            log.debug("The request for user with email = [{}] and user with id = [{}] with status ACCEPTED already exists", fromEmail, toUserId);
            throw new ResponseStatusException(BAD_REQUEST, "Необхідно завершити минулу співпрацю, щоб розпочати нову.");
        }
        if (usersAlreadyHasRequestWithStatus(fromEmail, toUserId, REJECTED)
                && notEnoughTimePassedFromLastRequestWithStatus(fromEmail, toUserId, REJECTED)) {
            log.debug("The request for user with email = [{}] and user with id = [{}] with status REJECTED already exists", fromEmail, toUserId);
            throw new ResponseStatusException(BAD_REQUEST, "Необхідно завершити минулу співпрацю, щоб розпочати нову.");
        }
        UserEntity userFrom = userRepository.findByEmail(fromEmail).orElse(null);
        if (isNull(userFrom)) {
            log.warn("Cannot find userFrom by email = [{}]", fromEmail);
            throw new ResponseStatusException(BAD_REQUEST, "Не вдається надіслати запит. Спробуйте ще раз пізніше.");
        }
        if (userHasNotActiveStatus(userFrom)) {
            log.debug("The userFrom has not ACTIVE status");
            throw new ResponseStatusException(BAD_REQUEST, "Необхідно заповнити профіль, щоб відправляти запити.");
        }
        UserEntity userTo = userRepository.findById(toUserId).orElse(null);
        if (isNull(userTo)) {
            log.warn("Cannot find userTo by id = [{}]", toUserId);
            throw new ResponseStatusException(BAD_REQUEST, "Не вдається надіслати запит. Спробуйте ще раз пізніше.");
        }
        if (userHasNotActiveStatus(userTo)) {
            log.debug("The userTo has not ACTIVE status");
            throw new ResponseStatusException(BAD_REQUEST, "Користувач не заповнив профіль, тому йому не можна відправляти запити.");
        }
        if (toAndFromUserAreOneUser(userFrom, userTo)) {
            log.warn("Cannot send request to self");
            throw new ResponseStatusException(BAD_REQUEST, "Не можливо надіслати запит самому собі.");
        }
        if (toUserHasTheSameRole(userFrom, userTo)) {
            log.warn("Cannot send request to user with the same role");
            throw new ResponseStatusException(BAD_REQUEST, "Не можливо надіслати запит користувачу, який має тип профілю, як і Ваш.");
        }
        MentoringRequestEntity mentoringRequestEntity = createMentoringRequestEntity(userFrom, userTo);
        return mentoringRequestMapper.entityToDto(mentoringRequestRepository.save(mentoringRequestEntity));
    }

    private boolean userHasMoreThanAvailablePendingRequests(String fromEmail) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTimestampNow());
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        zonedDateTime = zonedDateTime.minusDays(1);
        return mentoringRequestRepository.countPendingRequestsPerDayByUser(fromEmail, Timestamp.valueOf(zonedDateTime.toLocalDateTime())) >= NUMBER_AVAILABLE_PENDING_REQUESTS;
    }

    private boolean userHasMoreThanAvailableAcceptedRequests(int userCount) {
        return userCount >= NUMBER_AVAILABLE_ACCEPTED_REQUESTS;
    }

    private int getCountByUserEmailAndStatus(String fromEmail) {
        return mentoringRequestRepository.countByUserEmailAndStatus(fromEmail, ACCEPTED);
    }

    private boolean notEnoughTimePassedFromLastRequestWithStatus(String fromEmail, Long toUserId, MentoringRequestStatus status) {
        Timestamp now = getTimestampNow();
        Timestamp lastUpdateDate = mentoringRequestRepository.findLastRequestTimeForUsersByStatus(toUserId, fromEmail, status);
        return now.after(getTimeAfterCoolDown(lastUpdateDate));
    }

    private Timestamp getTimeAfterCoolDown(Timestamp lastUpdateDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastUpdateDate);
        cal.add(DAY_OF_WEEK, 3);
        return new Timestamp(cal.getTimeInMillis());
    }

    private boolean userHasNotActiveStatus(UserEntity userTo) {
        return !ACTIVE.equals(userTo.getStatus());
    }

    private boolean toAndFromUserAreOneUser(UserEntity userFrom, UserEntity userTo) {
        return userFrom.equals(userTo);
    }

    private boolean toUserHasTheSameRole(UserEntity userFrom, UserEntity userTo) {
        return userFrom.getRole().getName().equals(userTo.getRole().getName());
    }

    private boolean usersAlreadyHasRequestWithStatus(String fromEmail, Long toUserId, MentoringRequestStatus status) {
        return mentoringRequestRepository.existsByTwoUsersAndStatus(toUserId, fromEmail, status);
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
    public MentoringRequestDTO acceptMentoringReqStatusById(Long reqId, String email) {
        log.debug("Accepting mentoring request by id = [{}]", reqId);
        MentoringRequestEntity mentoringReqEntity = getMentoringRequestEntityById(reqId);
        final MentoringRequestStatus oldStatus = mentoringReqEntity.getStatus();
        final MentoringRequestStatus newStatus = ACCEPTED;
        if (isPending(oldStatus) && statusesNotEquals(oldStatus, newStatus)) {
            if (checkIfUserHasRights(email, mentoringReqEntity.getTo().getEmail())) {
                int countByReceiver = getCountByUserEmailAndStatus(email);
                if (userHasMoreThanAvailableAcceptedRequests(countByReceiver)) {
                    log.debug("Receiver = [{}] can't send more than number of available requests", email);
                    throw new ResponseStatusException(BAD_REQUEST, "Ви перевищили кількість відкритих співпраць. " +
                            "Вам доступно лише 2 співпраці, щоб надіслати ще, необхідно завершити повередні.");
                }
                String fromEmail = mentoringReqEntity.getFrom().getEmail();
                int countBySender = getCountByUserEmailAndStatus(fromEmail);
                if (userHasMoreThanAvailableAcceptedRequests(countBySender)) {
                    log.debug("Sender = [{}] can't send more than number of available requests", fromEmail);
                    throw new ResponseStatusException(BAD_REQUEST, "На жаль неможливо прийняти запит від цього " +
                            "користувача, оскільки він перевищив кількість активних співпраць.");
                }
                MentoringRequestDTO updatedMentoringReq = mentoringRequestMapper.entityToDto(
                        updateMentoringRequestStatus(mentoringReqEntity, newStatus));
                cancelAllPendingReqsForUserIfHaxMaxNumberAcceptedReq(email, countByReceiver);
                cancelAllPendingReqsForUserIfHaxMaxNumberAcceptedReq(fromEmail, countByReceiver);
                return updatedMentoringReq;
            }
            logAndThrowForbiddenException(NO_RIGHTS_TO_ACCEPT);
        }
        logAndThrowProcessedReqException("Не вдається прийняти запит, схоже він був уже опрацьований.");
        return null;
    }

    private void cancelAllPendingReqsForUserIfHaxMaxNumberAcceptedReq(String email, int nowNumber) {
        if (nowNumber == NUMBER_AVAILABLE_ACCEPTED_REQUESTS) {
            mentoringRequestRepository.cancelAllPendingRequestsByUserEmail(email);
        }
    }

    @Override
    public MentoringRequestDTO rejectMentoringReqStatusById(Long reqId, String email) {
        log.debug("Rejecting mentoring request by id = [{}]", reqId);
        MentoringRequestEntity mentoringReqEntity = getMentoringRequestEntityById(reqId);
        final MentoringRequestStatus oldStatus = mentoringReqEntity.getStatus();
        final MentoringRequestStatus newStatus = REJECTED;
        if (isPending(oldStatus) && statusesNotEquals(oldStatus, newStatus)) {
            if (checkIfUserHasRights(email, mentoringReqEntity.getTo().getEmail())) {
                return mentoringRequestMapper.entityToDto(updateMentoringRequestStatus(mentoringReqEntity, newStatus));
            }
            logAndThrowForbiddenException(NO_RIGHTS_TO_REJECT);
        }
        logAndThrowProcessedReqException("Не вдається відхилити запит, схоже він був уже опрацьований.");
        return null;
    }

    @Override
    public MentoringRequestDTO cancelMentoringReqStatusById(Long reqId, String email) {
        log.debug("Canceling mentoring request by id = [{}]", reqId);
        MentoringRequestEntity mentoringReqEntity = getMentoringRequestEntityById(reqId);
        final MentoringRequestStatus oldStatus = mentoringReqEntity.getStatus();
        final MentoringRequestStatus newStatus = CANCELED;
        if (isPending(oldStatus) && statusesNotEquals(oldStatus, newStatus)) {
            if (checkIfUserHasRights(email, mentoringReqEntity.getFrom().getEmail())) {
                return mentoringRequestMapper.entityToDto(updateMentoringRequestStatus(mentoringReqEntity, newStatus));
            }
            logAndThrowForbiddenException(NO_RIGHTS_TO_CANCEL);
        }
        logAndThrowProcessedReqException("Не вдається відмінити запит, схоже він був уже опрацьований.");
        return null;
    }

    @Override
    public MentoringRequestDTO finishMentoringReqStatusById(Long reqId, String email) {
        log.debug("Finishing mentoring request by id = [{}]", reqId);
        MentoringRequestEntity mentoringReqEntity = getMentoringRequestEntityById(reqId);
        final MentoringRequestStatus oldStatus = mentoringReqEntity.getStatus();
        final MentoringRequestStatus newStatus = FINISHED;
        if (isAccepted(oldStatus) && statusesNotEquals(oldStatus, newStatus)) {
            if (checkIfUserHasRights(email, mentoringReqEntity.getFrom().getEmail()) ||
                    checkIfUserHasRights(email, mentoringReqEntity.getTo().getEmail())) {
                return mentoringRequestMapper.entityToDto(updateMentoringRequestStatus(mentoringReqEntity, newStatus));
            }
            logAndThrowForbiddenException(NO_RIGHTS_TO_FINISH);
        }
        logAndThrowProcessedReqException("Не вдається закінчити співпрацю.");
        return null;
    }

    private boolean isAccepted(MentoringRequestStatus oldStatus) {
        return ACCEPTED.equals(oldStatus);
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

    private MentoringRequestEntity updateMentoringRequestStatus(MentoringRequestEntity mentoringReqEntity, MentoringRequestStatus newStatus) {
        mentoringReqEntity.setStatus(newStatus);
        mentoringReqEntity.setUpdatedAt(getTimestampNow());
        return mentoringRequestRepository.save(mentoringReqEntity);
    }

    private void logAndThrowForbiddenException(String exMessage) {
        log.debug("The user has not right to change status");
        throw new ResponseStatusException(FORBIDDEN, exMessage);
    }

    private void logAndThrowProcessedReqException(String message) {
        log.debug("The mentoring request can't change status from Accepted or Rejected");
        throw new ResponseStatusException(BAD_REQUEST, message);
    }

    @Override
    public boolean checkIfUserHasPendingReqs(String userEmail) {
        log.debug("Checking if user has pending requests with email = [{}]", userEmail);
        return mentoringRequestRepository.existsPendingReqsToUserByEmail(userEmail);
    }
}
