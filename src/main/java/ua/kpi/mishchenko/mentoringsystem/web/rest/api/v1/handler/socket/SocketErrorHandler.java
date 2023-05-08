package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.handler.socket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
public class SocketErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        Throwable exception = ex;
        if (exception instanceof MessageDeliveryException) {
            exception = exception.getCause();
        }

        if (exception instanceof AccessDeniedException) {
            return handleAccessDeniedException(clientMessage);
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private static Message<byte[]> handleAccessDeniedException(Message<byte[]> clientMessage) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage("400");
        accessor.setLeaveMutable(true);
        accessor.setReceiptId(String.valueOf(clientMessage.getHeaders().get("simpSessionId")));
        return MessageBuilder.createMessage("You don't have access to this".getBytes(), accessor.getMessageHeaders());
    }
}
