package ua.kpi.mishchenko.mentoringsystem.web.socket.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class SocketErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        if (ex instanceof MessageDeliveryException) {
            ex = ex.getCause();
        }

        if (ex instanceof AccessDeniedException) {
            return handleAccessDeniedException(clientMessage);
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private static Message<byte[]> handleAccessDeniedException(Message<byte[]> clientMessage) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage("400");
        accessor.setLeaveMutable(true);
        accessor.setReceiptId(String.valueOf(clientMessage.getHeaders().get("simpSessionId")));
        String simpDestination = clientMessage.getHeaders().get("simpDestination", String.class);
        if (isNull(simpDestination)) {
            return MessageBuilder.createMessage("You don't have access to this".getBytes(), accessor.getMessageHeaders());
        }
        return MessageBuilder.createMessage(simpDestination.getBytes(), accessor.getMessageHeaders());
    }
}
