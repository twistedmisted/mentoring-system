package ua.kpi.mishchenko.mentoringsystem.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.entity.redis.hash.JwtTokenHash;

import java.util.List;
import java.util.Optional;

@Repository
public interface JwtTokenRepository extends CrudRepository<JwtTokenHash, String> {

    Optional<JwtTokenHash> findByToken(String token);

    List<JwtTokenHash> findAllByEmail(String email);

    boolean existsByToken(String token);
}
