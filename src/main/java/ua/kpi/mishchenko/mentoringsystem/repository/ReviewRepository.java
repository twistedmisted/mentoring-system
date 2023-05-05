package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.entity.ReviewEntity;

@Repository
public interface ReviewRepository extends CrudRepository<ReviewEntity, Long> {

    Page<ReviewEntity> findAllByToUserId(Long userId, Pageable pageable);

    boolean existsByToUserId(Long userId);

    @Query("SELECT avg(r.rating) FROM ReviewEntity r WHERE r.toUser.id = :userId")
    double calculateAvgRatingByToUserId(@Param(value = "userId") Long userId);

    @Query("SELECT (SELECT COUNT(r) = 0" +
            "       FROM ReviewEntity r " +
            "       WHERE r.mentoringRequest.id = m.id " +
            "         AND r.fromUser.email = :fromUserEmail " +
            "         AND r.toUser.id = :toUserId) " +
            "FROM MentoringRequestEntity m " +
            "WHERE ((m.from.email = :fromUserEmail AND m.to.id = :toUserId) " +
            "OR (m.from.id = :toUserId AND m.to.email = :fromUserEmail)) " +
            "AND m.status = ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus.FINISHED " +
            "ORDER BY m.updatedAt DESC " +
            "LIMIT 1 ")
    boolean checkIfUserCanWriteReview(String fromUserEmail, Long toUserId);
}
