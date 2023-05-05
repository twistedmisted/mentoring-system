package ua.kpi.mishchenko.mentoringsystem.facade;

import org.springframework.messaging.simp.user.SimpSubscription;

import java.util.Set;

public interface SocketSystemFacade {

    void sendToUserBySessionId(String sessionId, String destination, Object payload);

    void sendToUserSessionsByEmail(String email, String destination, Object payload);

    Set<SimpSubscription> findSubsByDestEndWithAndUserEmail(String endsWith, String email);

    Set<SimpSubscription> findSubsByDestinationEndWith(String endsWith);
}
