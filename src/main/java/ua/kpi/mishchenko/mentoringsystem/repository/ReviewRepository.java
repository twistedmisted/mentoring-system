package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.ReviewEntity;

@Repository
public interface ReviewRepository extends CrudRepository<ReviewEntity, Long> {
}
