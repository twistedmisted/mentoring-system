package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MessageRequest;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class MessageController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MessageRequest send(@Payload MessageRequest message) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new MessageRequest(message.getToUserId(), message.getText(), time);
    }
}
