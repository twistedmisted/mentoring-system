package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;
import ua.kpi.mishchenko.mentoringsystem.entity.MentoringRequestEntity;

@Repository
public interface MentoringRequestRepository extends CrudRepository<MentoringRequestEntity, Long> {

    Page<MentoringRequestEntity> findAll(Specification<MentoringRequestEntity> specification, Pageable pageable);

    boolean existsByFromEmailAndToIdAndStatus(String fromEmail, Long toUserId, MentoringRequestStatus status);

    boolean existsByToIdAndFromIdAndStatus(Long toUserId, Long fromUserId, MentoringRequestStatus status);
}
