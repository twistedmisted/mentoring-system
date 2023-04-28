package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MessageDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.MessageMapper;
import ua.kpi.mishchenko.mentoringsystem.entity.MessageEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.MessageRepository;
import ua.kpi.mishchenko.mentoringsystem.service.MessageService;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.lessThanOne;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private static final int PAGE_SIZE = 10;

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public PageBO<MessageDTO> getMessagesByChatId(Long chatId, int numberOfPage) {
        log.debug("Getting messages by chat id = [{}]", chatId);
        if (lessThanOne(numberOfPage)) {
            log.warn("The number of page and size of page must be greater than zero");
            throw new ResponseStatusException(BAD_REQUEST, "Номер сторінки не може бути менше 1.");
        }
        Page<MessageEntity> messagePage = messageRepository.findAllByChatId(chatId,
                PageRequest.of(numberOfPage - 1, PAGE_SIZE, Sort.by(DESC, "createdAt")));
        if (!messagePage.hasContent()) {
            log.debug("Cannot find messages by chat id = [{}] on page = [{}]", chatId, numberOfPage);
            return new PageBO<>(numberOfPage, messagePage.getTotalPages());
        }
        List<MessageDTO> messageDtos = messagePage.getContent()
                .stream()
                .map(messageMapper::entityToDto)
                .toList();
        return new PageBO<>(messageDtos, numberOfPage, messagePage.getTotalPages());
    }

    @Override
    public void saveMessage(MessageDTO message) {

    }
}
