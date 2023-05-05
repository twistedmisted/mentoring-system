package ua.kpi.mishchenko.mentoringsystem.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import ua.kpi.mishchenko.mentoringsystem.facade.SocketSystemFacade;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocketSystemFacadeImpl implements SocketSystemFacade {

    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendToUserBySessionId(String sessionId, String destination, Object payload) {
        simpMessagingTemplate.convertAndSendToUser(sessionId, destination, payload, createHeaders(sessionId));
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    @Override
    public void sendToUserSessionsByEmail(String email, String destination, Object payload) {
        Set<SimpSubscription> subscriptions = findSubsByDestEndWithAndUserEmail(destination, email);
        for (SimpSubscription subscription : subscriptions) {
            sendToUserBySessionId(subscription.getSession().getId(), destination, payload);
        }
    }

    @Override
    public Set<SimpSubscription> findSubsByDestEndWithAndUserEmail(String endsWith, String email) {
        return simpUserRegistry.findSubscriptions(s -> s.getDestination().endsWith(endsWith)
                && s.getSession().getUser().getName().equals(email));
    }

    @Override
    public Set<SimpSubscription> findSubsByDestinationEndWith(String endsWith) {
        return simpUserRegistry.findSubscriptions(s -> s.getDestination().endsWith(endsWith));
    }
}
