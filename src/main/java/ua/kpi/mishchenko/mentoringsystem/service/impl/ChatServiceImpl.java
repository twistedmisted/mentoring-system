package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.ChatDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.ChatMapper;
import ua.kpi.mishchenko.mentoringsystem.entity.ChatEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.ChatRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.PrivateChat;
import ua.kpi.mishchenko.mentoringsystem.service.ChatService;

import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.lessThanOne;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private static final int PAGE_SIZE = 10;

    private final ChatRepository chatRepository;
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
                PageRequest.of(numberOfPage - 1, PAGE_SIZE, Sort.by(DESC, "lastMessageCreatedAt")));
        if (!chatPage.hasContent()) {
            log.debug("Cannot find chats for user with email = [{}]", email);
            return new PageBO<>(numberOfPage, chatPage.getTotalPages());
        }
        return new PageBO<>(chatPage.getContent(), numberOfPage, chatPage.getTotalPages());
    }

    @Override
    public void createChat(ChatDTO chat) {
        log.debug("Creating chat");
        Optional<Boolean> existsChatWithUsers = chatRepository.existsChatWithUsers(chat.getUsers().stream().map(UserDTO::getId).toList());
        if (existsChatWithUsers.isPresent() && existsChatWithUsers.get()) {
            log.debug("Users already has private chat");
            return;
        }
        ChatEntity entity = chatMapper.dtoToEntity(chat);
        chatRepository.save(entity);
    }

    @Override
    public boolean userHasAccessToChat(Long chatId, String reqEmail) {
        log.debug("Checking if user with email = [{}] has access to chat with id = [{}]", reqEmail, chatId);
        return chatRepository.existsByIdAndUsersEmail(chatId, reqEmail);
    }
}
