package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.QuestionnaireEntity;

@Repository
public interface QuestionnaireRepository extends CrudRepository<QuestionnaireEntity, Long> {
}
