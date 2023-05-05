package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.interceptor.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ua.kpi.mishchenko.mentoringsystem.facade.NotificationSystemFacade;

import java.util.Set;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class NotificationSubscribeEvent implements ApplicationListener<SessionSubscribeEvent> {

    private static final String QUEUE_NOTIFICATIONS_DESTINATION = "/queue/notifications";

    private final SimpUserRegistry simpUserRegistry;
    private final NotificationSystemFacade notificationSystemFacade;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headers.getDestination();
        if (isNull(destination)) {
            return;
        }
        if (isNotificationsQueue(destination)) {
            if (isNull(event.getUser())) {
                throw new AccessDeniedException("Can't find authorized user");
            }
            String eventUserEmail = event.getUser().getName();
            Set<SimpSubscription> subscriptions = getNotificationSubs(eventUserEmail);
            for (SimpSubscription subscription : subscriptions) {
                String sessionId = subscription.getSession().getId();
                notificationSystemFacade.sendReqNotificationIfExists(eventUserEmail, sessionId);
                notificationSystemFacade.sendMessageNotificationIfExists(eventUserEmail, sessionId);
            }
        }
    }

    private boolean isNotificationsQueue(String destination) {
        return destination.endsWith(QUEUE_NOTIFICATIONS_DESTINATION);
    }

    private Set<SimpSubscription> getNotificationSubs(String eventUserEmail) {
        return simpUserRegistry.findSubscriptions(
                s -> isEndsWith(s.getDestination()) && isSubscribeEventUser(eventUserEmail, s.getSession().getUser().getName()));
    }

    private boolean isEndsWith(String destination) {
        return isNotificationsQueue(destination);
    }

    private boolean isSubscribeEventUser(String eventUserEmail, String sessionUserEmail) {
        return sessionUserEmail.equals(eventUserEmail);
    }
}
