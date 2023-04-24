package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.ReviewEntity;

@Repository
public interface ReviewRepository extends CrudRepository<ReviewEntity, Long> {

    Page<ReviewEntity> findAllByToUserId(Long userId, Pageable pageable);

    boolean existsByToUserIdAndFromUserId(Long toUserId, Long fromUserId);

    boolean existsByToUserId(Long userId);

    @Query("SELECT avg(r.rating) FROM ReviewEntity r WHERE r.toUser.id = :userId")
    double calculateAvgRatingByToUserId(@Param(value = "userId") Long userId);
}
