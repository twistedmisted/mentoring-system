package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.ChatDTO;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.PrivateChat;

public interface ChatService {

    PrivateChat getChatById(Long chatId, String reqEmail);

    PageBO<PrivateChat> getChatsByUserEmail(String email, int numberOfPage);

    ChatDTO createChat(ChatDTO chat);

    boolean userHasAccessToChat(Long chatId, String reqEmail);

    void archiveChatByMentoringReqId(Long reqId);

    boolean isArchived(Long chatId);
}
