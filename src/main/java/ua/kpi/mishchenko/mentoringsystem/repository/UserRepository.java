package ua.kpi.mishchenko.mentoringsystem.repository;

import jakarta.transaction.Transactional;
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
import ua.kpi.mishchenko.mentoringsystem.repository.projection.UserEmailProjection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Set<UserEntity> findAllByIdIn(List<Long> ids);

    Page<UserEntity> findAll(Specification<UserEntity> specification, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByIdAndEmail(Long id, String email);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserEntity e SET e.status = :status WHERE e.id = :id")
    void updateStatusByUserId(@Param(value = "id") Long userId, @Param(value = "status") UserStatus status);

    @Query("SELECT u.email AS email " +
            "FROM UserEntity u " +
            "JOIN u.chats c " +
            "WHERE c.id = :chatId")
    List<UserEmailProjection> findAllUserEmailsByChatId(Long chatId);

    List<UserEmailProjection> findAllByEmailNotAndChatsId(String email, Long chatId);
}
