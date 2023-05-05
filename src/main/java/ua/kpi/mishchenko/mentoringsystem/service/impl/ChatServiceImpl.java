package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.ChatDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.ChatMapper;
import ua.kpi.mishchenko.mentoringsystem.domain.util.ChatStatus;
import ua.kpi.mishchenko.mentoringsystem.entity.ChatEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.ChatRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.MentoringRequestRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.PrivateChat;
import ua.kpi.mishchenko.mentoringsystem.service.ChatService;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.ChatStatus.ARCHIVED;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.lessThanOne;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private static final int PAGE_SIZE = 15;

    private final ChatRepository chatRepository;
    private final MentoringRequestRepository mentoringRequestRepository;
    private final ChatMapper chatMapper;

    @Override
    public PrivateChat getChatById(Long chatId, String reqEmail) {
        log.debug("Getting chat by id = [{}]", chatId);
        return chatRepository.findProjections(chatId, reqEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Не вдається знайти такий чат."));
    }

    @Override
    public PageBO<PrivateChat> getChatsByUserEmail(String email, int numberOfPage) {
        log.debug("Getting chats by user email = [{}]", email);
        if (lessThanOne(numberOfPage)) {
            log.warn("The number of page and size of page must be greater than zero");
            throw new ResponseStatusException(BAD_REQUEST, "Номер сторінки не може бути менше 1.");
        }
        Page<PrivateChat> chatPage = chatRepository.findAllProjections(email,
                PageRequest.of(numberOfPage - 1, PAGE_SIZE));
        if (!chatPage.hasContent()) {
            log.debug("Cannot find chats for user with email = [{}]", email);
            return new PageBO<>(numberOfPage, chatPage.getTotalPages());
        }
        return new PageBO<>(chatPage.getContent(), numberOfPage, chatPage.getTotalPages());
    }

    @Override
    public ChatDTO createChat(ChatDTO chat) {
        log.debug("Creating chat");
        ChatEntity chatEntity = chatRepository.findByUsersIdIn(chat.getUsers()
                        .stream()
                        .map(UserDTO::getId)
                        .toList())
                .orElse(null);
        if (isNull(chatEntity)) {
            chatEntity = chatMapper.dtoToEntity(chat);
        } else if (chatEntity.getStatus().equals(ARCHIVED)) {
            chatEntity.setStatus(ChatStatus.ACTIVE);
            chatEntity.addMentoringRequest(mentoringRequestRepository.findById(chat.getMentoringReqIds().get(0)).get());
        } else {
            log.debug("Chat is already active");
            return chat;
        }
        return chatMapper.entityToDto(chatRepository.save(chatEntity));
    }

    private boolean existsById(Long id) {
        return chatRepository.existsById(id);
    }

    @Override
    public boolean userHasAccessToChat(Long chatId, String reqEmail) {
        log.debug("Checking if user with email = [{}] has access to chat with id = [{}]", reqEmail, chatId);
        return chatRepository.existsByIdAndUsersEmail(chatId, reqEmail);
    }

    @Override
    public void archiveChatByMentoringReqId(Long reqId) {
        log.debug("Archiving chat by mentoring request with id = [{}]", reqId);
        chatRepository.updateChatStatusByMentoringReqId(reqId);
    }

    @Override
    public boolean isArchived(Long chatId) {
        log.debug("Get boolean value is chat archived");
        return chatRepository.existsByIdAndStatus(chatId, ARCHIVED);
    }
}
