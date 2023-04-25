package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MessageDTO;
import ua.kpi.mishchenko.mentoringsystem.repository.MessageRepository;
import ua.kpi.mishchenko.mentoringsystem.service.MessageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public void saveMessage(MessageDTO message) {

    }
}
