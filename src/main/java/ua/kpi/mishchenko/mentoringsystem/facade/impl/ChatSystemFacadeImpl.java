package ua.kpi.mishchenko.mentoringsystem.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.ChatBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MessageDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.ChatUser;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MessageRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.PrivateMessageResponse;
import ua.kpi.mishchenko.mentoringsystem.facade.ChatSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.facade.NotificationSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.facade.SocketSystemFacade;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.PrivateChat;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.PrivateMessage;
import ua.kpi.mishchenko.mentoringsystem.service.ChatService;
import ua.kpi.mishchenko.mentoringsystem.service.MessageService;
import ua.kpi.mishchenko.mentoringsystem.service.S3Service;
import ua.kpi.mishchenko.mentoringsystem.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MessageStatus.SENT;
import static ua.kpi.mishchenko.mentoringsystem.domain.util.MessageStatus.UNSENT;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.ISOStringToTimestamp;
import static ua.kpi.mishchenko.mentoringsystem.util.Util.parseTimestampToISO8601String;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSystemFacadeImpl implements ChatSystemFacade {

    private static final String QUEUE_CHATS_DESTINATION = "/queue/chats";

    private final UserService userService;
    private final MessageService messageService;
    private final ChatService chatService;
    private final S3Service s3Service;

    private final SocketSystemFacade socketSystemFacade;
    private final NotificationSystemFacade notificationSystemFacade;

    @Override
    public ChatBO getChatById(Long chatId, String reqEmail) {
        log.debug("Getting chat by id = [{}]", chatId);
        if (!userHasAccessToChat(chatId, reqEmail)) {
            throw new ResponseStatusException(FORBIDDEN, "Схоже Ви не маєте доступу до цього чату.");
        }
        PrivateChat privateChat = chatService.getChatById(chatId, reqEmail);
        return createChatBo(privateChat);
    }

    private boolean userHasAccessToChat(Long chatId, String reqEmail) {
        return chatService.userHasAccessToChat(chatId, reqEmail);
    }

    private ChatBO createChatBo(PrivateChat privateChat) {
        ChatBO chatBo = new ChatBO();
        chatBo.setId(privateChat.getId());
        chatBo.setTitle(privateChat.getTitle());
        chatBo.setPhotoUrl(getProfilePhotoUrlByUserId(privateChat.getToUserId()));
        chatBo.setLastMessageDate(parseTimestampToISO8601String(privateChat.getLastMessageCreatedAt()));
        chatBo.setLastMessageText(privateChat.getLastMessageText());
        chatBo.setUnreadMessages(privateChat.getUnreadMessages());
        chatBo.setStatus(privateChat.getStatus());
        return chatBo;
    }

    private String getProfilePhotoUrlByUserId(Long userId) {
        return s3Service.getUserPhoto(userId);
    }

    @Override
    public PageBO<ChatBO> getChatsByUserEmail(String email, int numberOfPage) {
        log.debug("Getting chats by user email = [{}]", email);
        PageBO<PrivateChat> chatPage = chatService.getChatsByUserEmail(email, numberOfPage);
        PageBO<ChatBO> resChatPage = new PageBO<>(chatPage.getCurrentPageNumber(), chatPage.getTotalPages());
        for (PrivateChat privateChat : chatPage.getContent()) {
            resChatPage.addElement(createChatBo(privateChat));
        }
        return resChatPage;
    }

    @Override
    public void processMessageSend(Long chatId,
                                   MessageRequest messageRequest,
                                   String senderEmail,
                                   SimpMessageHeaderAccessor headerAccessor) {
        sendMessageOrNotification(chatId, messageRequest, senderEmail, headerAccessor);
        sendChatUpdate(chatId, getAllUserEmailsByChatId(chatId));
    }

    private List<String> getAllUserEmailsByChatId(Long chatId) {
        log.debug("Getting all chat members by chat id = [{}]", chatId);
        return userService.getAllChatUsersByChatId(chatId);
    }

    private void sendMessageOrNotification(Long chatId, MessageRequest message, String senderEmail, SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO messageDTO = saveMessage(chatId, message, senderEmail);
        PrivateMessageResponse messageToSend = createAndSendMessage(messageDTO);
        final String chatDestination = "/queue/chat/" + chatId + "/message";
        Set<SimpSubscription> chatMessageSubs = socketSystemFacade.findSubsByDestinationEndWith(chatDestination);
        boolean messageSent = false;
        for (SimpSubscription subscription : chatMessageSubs) {
            String subSessionId = subscription.getSession().getId();
            if (isSender(headerAccessor.getSessionId(), subSessionId)) {
                continue;
            }
            socketSystemFacade.sendToUserBySessionId(subSessionId, chatDestination, messageToSend);
            updateMessageStatus(messageDTO.getId());
            messageSent = true;
        }
        if (!messageSent) {
            sendNotification(messageDTO.getFromUser().getEmail(), messageDTO.getChatId());
        }
    }

    private MessageDTO saveMessage(Long chatId, MessageRequest messageRequest, String fromEmail) {
        log.debug("Saving message for chatId = [{}] fromEmail = [{}]", chatId, fromEmail);
        MessageDTO messageDTO = createMessageDto(chatId, messageRequest, fromEmail);
        return messageService.saveMessage(messageDTO);
    }

    private MessageDTO createMessageDto(Long chatId, MessageRequest messageRequest, String fromEmail) {
        MessageDTO messageDto = new MessageDTO();
        messageDto.setChatId(chatId);
        messageDto.setId(Long.valueOf(messageRequest.getId()));
        messageDto.setText(messageRequest.getText().trim());
        messageDto.setCreatedAt(ISOStringToTimestamp(messageRequest.getCreatedAt()));
        messageDto.setFromUser(UserDTO.builder().email(fromEmail).build());
        messageDto.setStatus(UNSENT);
        return messageDto;
    }

    private boolean isSender(String senderSessionId, String subSessionId) {
        return subSessionId.equals(senderSessionId);
    }

    private void sendChatUpdate(Long chatId, List<String> chatMembersEmails) {
        Set<SimpSubscription> chatsSubs = socketSystemFacade.findSubsByDestinationEndWith(QUEUE_CHATS_DESTINATION);
        chatsSubs.stream()
                .filter(s -> chatMembersEmails.contains(s.getSession().getUser().getName()))
                .forEach(s -> createAndSendChatUpdate(s.getSession(), chatId));
    }

    private void createAndSendChatUpdate(SimpSession session, Long chatId) {
        ChatBO chat = getChatById(chatId, session.getUser().getName());
        socketSystemFacade.sendToUserBySessionId(session.getId(), QUEUE_CHATS_DESTINATION, chat);
    }

    private PrivateMessageResponse createAndSendMessage(MessageDTO messageDTO) {
        return PrivateMessageResponse.builder()
                .id(messageDTO.getId())
                .text(messageDTO.getText())
                .date(parseTimestampToISO8601String(messageDTO.getCreatedAt()))
                .from(ChatUser.valueOf(messageDTO.getFromUser()))
                .chatId(messageDTO.getChatId())
                .build();
    }

    private void updateMessageStatus(Long messageId) {
        log.debug("Updating status for message with id = [{}], status = [{}]", messageId, SENT);
        messageService.updateMessageStatus(messageId, SENT);
    }

    private void sendNotification(String senderEmail, Long chatId) {
        List<String> usersEmails = userService.getUsersEmailsFromChatByIdExceptSender(chatId, senderEmail);
        for (String email : usersEmails) {
            notificationSystemFacade.sendNotificationByUserEmail(email, "Ви отримали нове повідомлення.");
        }
    }

    @Override
    public void updateChatInList(Long chatId, List<String> userEmails) {
        sendChatUpdate(chatId, userEmails);
    }

    @Override
    public PageBO<PrivateMessageResponse> getMessagesByChatId(Long chatId, int numberOfPage, String reqUserEmail) {
        log.debug("Getting messages by chat id = [{}]", chatId);
        PageBO<PrivateMessage> messagePage = messageService.getMessagesByChatId(chatId, numberOfPage);
        PageBO<PrivateMessageResponse> resMessagePage = new PageBO<>(messagePage.getCurrentPageNumber(), messagePage.getTotalPages());
        List<Long> unreadMessageIds = new ArrayList<>();
        for (PrivateMessage message : messagePage.getContent()) {
            if (message.getStatus().equals(UNSENT) && !message.getChatUser().getEmail().equals(reqUserEmail)) {
                unreadMessageIds.add(message.getId());
            }
            resMessagePage.addElement(createPrivateMessageResponse(message));
        }
        messageService.updateMessagesStatus(unreadMessageIds, SENT);
        return resMessagePage;
    }

    private PrivateMessageResponse createPrivateMessageResponse(PrivateMessage message) {
        return PrivateMessageResponse.builder()
                .id(message.getId())
                .text(message.getText())
                .date(parseTimestampToISO8601String(message.getCreatedAt()))
                .from(ChatUser.valueOf(message.getChatUser()))
                .build();
    }
}
