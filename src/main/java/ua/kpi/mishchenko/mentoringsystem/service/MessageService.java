package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MessageDTO;

public interface MessageService {

    PageBO<MessageDTO> getMessagesByChatId(Long chatId, int numberOfPage);

    void saveMessage(MessageDTO message);
}
