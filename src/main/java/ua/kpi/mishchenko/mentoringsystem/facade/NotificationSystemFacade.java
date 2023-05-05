package ua.kpi.mishchenko.mentoringsystem.facade;

public interface NotificationSystemFacade {

    void sendReqNotificationIfExists(String userEmail, String id);

    void sendMessageNotificationIfExists(String userEmail, String id);

    void sendNotificationByUserEmail(String email, String text);
}
