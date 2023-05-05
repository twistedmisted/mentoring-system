package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MessageDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MessageStatus;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.PrivateMessage;

import java.util.List;

public interface MessageService {

    PageBO<PrivateMessage> getMessagesByChatId(Long chatId, int numberOfPage);

    MessageDTO saveMessage(MessageDTO message);

    void updateMessageStatus(Long messageId, MessageStatus status);

    void updateMessagesStatus(List<Long> messageIds, MessageStatus status);

    boolean checkIfUserHasUnreadMessages(String userEmail);
}
