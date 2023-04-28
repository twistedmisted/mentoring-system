package ua.kpi.mishchenko.mentoringsystem.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.entity.redis.hash.JwtTokenHash;
import ua.kpi.mishchenko.mentoringsystem.repository.redis.JwtTokenRepository;
import ua.kpi.mishchenko.mentoringsystem.util.Util;

import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class JwtTokenService {

    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;
    private final Algorithm hmac512;
    private final JWTVerifier verifier;
    private final JwtTokenRepository jwtTokenRepository;

    public JwtTokenService(@Value("${jwt.secret}") final String secret, JwtTokenRepository jwtTokenRepository) {
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
        this.jwtTokenRepository = jwtTokenRepository;
    }

    public String generateToken(final Authentication authenticate) {
        String jwtToken = JWT.create()
                .withSubject(authenticate.getName())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .sign(this.hmac512);
        saveJwtTokenToDb(jwtToken, authenticate.getName());
        return jwtToken;
    }

    private void saveJwtTokenToDb(String jwtToken, String email) {
        JwtTokenHash jwtTokenHash = JwtTokenHash.builder()
                .id(Util.generateRandomUuid())
                .token(jwtToken)
                .email(email)
                .build();
        jwtTokenRepository.save(jwtTokenHash);
    }

    public String validateTokenAndGetUsername(final String token) {
        if (tokenNotExistsInDb(token)) {
            return null;
        }
        try {
            return verifier.verify(token).getSubject();
        } catch (final JWTVerificationException e) {
            jwtTokenRepository.delete(jwtTokenRepository.findByToken(token).get());
            return null;
        }
    }

    public boolean validateToken(String token) {
        if (tokenNotExistsInDb(token)) {
            return false;
        }
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private boolean tokenNotExistsInDb(String token) {
        return !jwtTokenRepository.existsByToken(token);
    }

    public void invalidateTokenByUserEmail(String email) {
        List<JwtTokenHash> tokens = jwtTokenRepository.findAllByEmail(email);
        jwtTokenRepository.deleteAll(tokens);
    }

    public void invalidateTokenByUserToken(String token) {
        JwtTokenHash jwtTokenHash = jwtTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(INTERNAL_SERVER_ERROR, "Не вдалося розлогінити користувача."));
        jwtTokenRepository.delete(jwtTokenHash);
    }
}
