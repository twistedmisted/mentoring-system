package ua.kpi.mishchenko.mentoringsystem.service.mail;

import ua.kpi.mishchenko.mentoringsystem.domain.dto.PasswordMessageInfo;

public interface EmailService {

    void sendRestorePasswordMessage(PasswordMessageInfo messageInfo);
}
