package ua.kpi.mishchenko.mentoringsystem.entity.redis.hash;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("JwtToken")
@Setter
@Getter
@Builder
public class JwtTokenHash implements Serializable {

    @Id
    private String id;

    @Indexed
    private String token;

    @Indexed
    private String email;
}
