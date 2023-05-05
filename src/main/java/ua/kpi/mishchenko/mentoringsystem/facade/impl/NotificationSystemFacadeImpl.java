package ua.kpi.mishchenko.mentoringsystem.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.NotificationPayload;
import ua.kpi.mishchenko.mentoringsystem.facade.NotificationSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.facade.SocketSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.service.MentoringRequestService;
import ua.kpi.mishchenko.mentoringsystem.service.MessageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSystemFacadeImpl implements NotificationSystemFacade {

    private static final String QUEUE_NOTIFICATIONS_DESTINATION = "/queue/notifications";
    private static final String PENDING_REQUESTS = "Ви маєте активні запити на менторство.";
    private static final String UNREAD_MESSAGE = "Ви маєте непрочитані повідомлення.";

    private final SocketSystemFacade socketSystemFacade;

    private final MessageService messageService;
    private final MentoringRequestService mentoringRequestService;

    @Override
    public void sendReqNotificationIfExists(String userEmail, String id) {
        if (checkIfUserHasPendingReqs(userEmail)) {
            socketSystemFacade.sendToUserBySessionId(
                    id,
                    QUEUE_NOTIFICATIONS_DESTINATION,
                    NotificationPayload.builder().text(PENDING_REQUESTS).build());
        }
    }

    private boolean checkIfUserHasPendingReqs(String userEmail) {
        log.debug("Checking if user = [{}] has pending reqs", userEmail);
        return mentoringRequestService.checkIfUserHasPendingReqs(userEmail);
    }

    @Override
    public void sendMessageNotificationIfExists(String userEmail, String id) {
        if (checkIfUserHasUnreadMessages(userEmail)) {
            socketSystemFacade.sendToUserBySessionId(
                    id,
                    QUEUE_NOTIFICATIONS_DESTINATION,
                    NotificationPayload.builder().text(UNREAD_MESSAGE).build());
        }
    }

    private boolean checkIfUserHasUnreadMessages(String userEmail) {
        log.debug("Checking if user = [{}] has unread messages", userEmail);
        return messageService.checkIfUserHasUnreadMessages(userEmail);
    }

    @Override
    public void sendNotificationByUserEmail(String email, String text) {
        socketSystemFacade.sendToUserSessionsByEmail(email, QUEUE_NOTIFICATIONS_DESTINATION, NotificationPayload.builder().text(text).build());
    }

}
