package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.dto.MessageDTO;

import java.util.List;

public interface MessageService {

    List<MessageDTO> getMessagesByChatId(Long chatId);

    void saveMessage(MessageDTO message);
}
