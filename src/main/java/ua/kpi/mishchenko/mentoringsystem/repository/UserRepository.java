package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;
import ua.kpi.mishchenko.mentoringsystem.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Page<UserEntity> findAll(Specification<UserEntity> specification, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByIdAndEmail(Long id, String email);

    @Modifying
    @Query("UPDATE UserEntity e SET e.status = :status WHERE e.id = :id")
    void updateStatusByUserId(@Param(value = "id") Long userId, @Param(value = "status") UserStatus status);
}
