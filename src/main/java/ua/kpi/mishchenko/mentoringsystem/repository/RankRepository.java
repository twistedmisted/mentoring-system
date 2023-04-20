package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.RankEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.RankNameOnly;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankRepository extends CrudRepository<RankEntity, Long> {

    Optional<RankEntity> findByName(String name);

    @Query(value = "SELECT r.name as name FROM RankEntity r ORDER BY r.level ASC")
    List<RankNameOnly> findProjections();
}
