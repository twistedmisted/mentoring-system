package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;
import ua.kpi.mishchenko.mentoringsystem.entity.MentoringRequestEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.MentoringReqIdAndExistsReview;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.MentoringRequestProjection;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface MentoringRequestRepository extends CrudRepository<MentoringRequestEntity, Long> {

    @Query("SELECT m.id AS id, m.from.id AS fromId, m.status AS status, m.from AS fromUser " +
            "FROM MentoringRequestEntity m " +
            "WHERE (m.to.id = :firstUserId AND m.from.email = :secondUserEmail) " +
            "OR (m.from.id = :firstUserId AND m.to.email = :secondUserEmail) " +
            "ORDER BY m.updatedAt DESC " +
            "LIMIT 1 ")
    Optional<MentoringRequestProjection> findLastRequestByUsers(Long firstUserId, String secondUserEmail);

    Page<MentoringRequestEntity> findAll(Specification<MentoringRequestEntity> specification, Pageable pageable);

    @Query("SELECT COUNT(m) > 0 FROM MentoringRequestEntity m " +
            "WHERE ((m.to.id = :firstUserId AND m.from.email = :secondUserEmail) " +
            "OR (m.from.id = :firstUserId AND m.to.email = :secondUserEmail)) " +
            "AND m.status = :status ")
    boolean existsByTwoUsersAndStatus(Long firstUserId, String secondUserEmail, MentoringRequestStatus status);

    @Query("SELECT COUNT(m) > 0 FROM MentoringRequestEntity m " +
            "WHERE m.to.id = :toUserId AND m.from.email = :fromEmail " +
            "AND m.status = ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.REJECTED ")
    boolean existsFromUserEmailToUserIdRejected(Long toUserId, String fromEmail);

    @Query("SELECT COUNT(m) > 0 FROM MentoringRequestEntity m " +
            "WHERE ((m.to.id = :firstUserId AND m.from.id = :secondUserId) " +
            "OR (m.from.id = :firstUserId AND m.to.id = :secondUserId)) " +
            "AND m.status = :status ")
    boolean existsByTwoUsersAndStatus(Long firstUserId, Long secondUserId, MentoringRequestStatus status);

    @Query("SELECT m.updatedAt FROM MentoringRequestEntity m " +
            "WHERE m.to.id = :toUserId AND m.from.email = :fromUserEmail " +
            "AND m.status = :status " +
            "ORDER BY m.updatedAt DESC " +
            "LIMIT 1 ")
    Timestamp findLastRequestTimeFromUserEmailToUserId(Long toUserId, String fromUserEmail, MentoringRequestStatus status);

    @Query("SELECT count(m) FROM MentoringRequestEntity m " +
            "WHERE (m.from.email = :email " +
            "OR m.to.email = :email) " +
            "AND m.status = ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.PENDING " +
            "AND m.createdAt > :dayBefore ")
    int countPendingRequestsPerDayByUser(String email, Timestamp dayBefore);

    @Query("SELECT count(m) FROM MentoringRequestEntity m " +
            "WHERE (m.from.email = :email " +
            "OR m.to.email = :email) " +
            "AND m.status = :status ")
    int countByUserEmailAndStatus(String email, MentoringRequestStatus status);

    @Query("SELECT m.id AS id, (SELECT COUNT(r) = 0" +
            "       FROM ReviewEntity r " +
            "       WHERE r.mentoringRequest.id = m.id " +
            "         AND r.fromUser.id = :fromUserId " +
            "         AND r.toUser.id = :toUserId) AS existsReview " +
            "FROM MentoringRequestEntity m " +
            "WHERE ((m.from.id = :fromUserId AND m.to.id = :toUserId) " +
            "OR (m.from.id = :toUserId AND m.to.id = :fromUserId)) " +
            "AND m.status = ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.FINISHED " +
            "ORDER BY m.updatedAt DESC " +
            "LIMIT 1 ")
    MentoringReqIdAndExistsReview findLastFinishedMentoringReqIdAndExistsReview(Long fromUserId, Long toUserId);

    @Modifying
    @Query("UPDATE MentoringRequestEntity m " +
            "SET m.status = ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.FINISHED " +
            "WHERE m.status = ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.PENDING " +
            "AND (m.from.id = (SELECT u.id FROM UserEntity u WHERE u.email = :email) OR m.to.id = (SELECT u.id FROM UserEntity u WHERE u.email = :email)) ")
    void cancelAllPendingRequestsByUserEmail(String email);

    List<MentoringRequestEntity> findAllByIdIn(List<Long> ids);

    @Query("SELECT count(m) > 0 " +
            "FROM MentoringRequestEntity m " +
            "WHERE m.to.email = :userEmail " +
            "AND m.status = ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.PENDING ")
    boolean existsPendingReqsToUserByEmail(String userEmail);
}
