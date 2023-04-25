package ua.kpi.mishchenko.mentoringsystem.service;

import ua.kpi.mishchenko.mentoringsystem.domain.dto.MessageDTO;

public interface MessageService {


    void saveMessage(MessageDTO message);
}
