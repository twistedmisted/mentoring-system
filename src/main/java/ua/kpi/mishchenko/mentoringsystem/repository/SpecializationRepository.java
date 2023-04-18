package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.SpecializationEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.SpecializationNameOnly;

import java.util.List;

@Repository
public interface SpecializationRepository extends CrudRepository<SpecializationEntity, Long> {

    @Query(value = "SELECT s.name as name FROM SpecializationEntity s ORDER BY s.name")
    List<SpecializationNameOnly> findProjections();
}
