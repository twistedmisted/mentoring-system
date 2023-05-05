package ua.kpi.mishchenko.mentoringsystem.facade;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.ChatBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MessageRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.PrivateMessageResponse;

import java.util.List;

public interface ChatSystemFacade {

    ChatBO getChatById(Long chatId, String email);

    PageBO<ChatBO> getChatsByUserEmail(String email, int numberOfPage);

    void processMessageSend(Long chatId, MessageRequest messageRequest, String senderEmail, SimpMessageHeaderAccessor headerAccessor);

    void addNewChatToPageIfSubscribed(Long chatId, List<String> userEmails);

    PageBO<PrivateMessageResponse> getMessagesByChatId(Long chatId, int numberOfPage, String principal);
}
