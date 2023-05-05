package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.interceptor.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.service.ChatService;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static org.springframework.messaging.simp.stomp.StompCommand.SEND;
import static org.springframework.messaging.simp.stomp.StompCommand.SUBSCRIBE;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscribeChannelInterceptorAdapter implements ChannelInterceptor {

    private static final Pattern CHAT_ID_PATTERN = Pattern.compile("/\\d+/");
    private static final Pattern CHAT_SUBSCRIBER_PATTERN = Pattern.compile("/user/queue/chat/[0-9]+/message");
    private static final Pattern MESSSAGE_SEND_PATTERN = Pattern.compile("/user/app/message/[0-9]+");

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        StompCommand command = accessor.getCommand();
        if (SUBSCRIBE == command) {
            Principal principal = accessor.getUser();
            if (isNull(principal)) {
                throw new AccessDeniedException("You don't have access to subscribe");
            }
            String destination = accessor.getDestination();
            if (isNull(destination)) {
                return message;
            }
            if (isChatSubscriber(destination)) {
                return processChatSub(message, principal, destination);
            }
        } else if (SEND == command) {
            Principal principal = accessor.getUser();
            if (isNull(principal)) {
                throw new AccessDeniedException("You don't have access to subscribe");
            }
            String destination = accessor.getDestination();
            if (isNull(destination)) {
                return message;
            }
            if (isMessageSend(destination)) {
                Long chatId = getChatIdFromDestination(destination);
                String userEmail = principal.getName();
                if (userHasNotAccessToChat(chatId, userEmail)) {
                    throw new AccessDeniedException("You don't have access to send messages to this chat.");
                }
            }
        }

        return message;
    }

    private Message<?> processChatSub(Message<?> message, Principal principal, String destination) {
        Long chatId = getChatIdFromDestination(destination);
        String userEmail = principal.getName();
        if (userHasNotAccessToChat(chatId, userEmail)) {
            throw new AccessDeniedException("You don't have access to this chat");
        }
        if (chatIsArchived(chatId)) {
            throw new AccessDeniedException("Chat is archived");
        }
        return message;
    }

    private boolean chatIsArchived(Long chatId) {
        return chatService.isArchived(chatId);
    }

    private Long getChatIdFromDestination(String destination) {
        Matcher matcher = CHAT_ID_PATTERN.matcher(destination);
        if (matcher.find()) {
            return Long.valueOf(matcher.group().replaceAll("/", ""));
        }
        throw new IllegalArgumentException("Cannot parse chat id from subscriber");
    }

    private boolean isChatSubscriber(String destination) {
        return CHAT_SUBSCRIBER_PATTERN.matcher(destination).matches();
    }

    private boolean isMessageSend(String destination) {
        return MESSSAGE_SEND_PATTERN.matcher(destination).matches();
    }

    private boolean userHasNotAccessToChat(Long chatId, String userEmail) {
        return !chatService.userHasAccessToChat(chatId, userEmail);
    }
}
