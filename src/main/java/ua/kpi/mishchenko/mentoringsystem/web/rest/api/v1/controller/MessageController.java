package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MessageRequest;
import ua.kpi.mishchenko.mentoringsystem.facade.MentoringSystemFacade;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MentoringSystemFacade mentoringSystemFacade;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MessageRequest send(@Payload MessageRequest message) {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new MessageRequest(message.getToUserId(), message.getText(), time);
    }

    @GetMapping("/api/v1/messages")
    public ResponseEntity<Map<String, Object>> getMessagesByChat(@RequestParam(value = "chat") Long chatId,
                                                                 @RequestParam(value = "page", required = false, defaultValue = "1")
                                                                 int numberOfPage) {
        log.debug("Getting all messages by chat id = [{}]", chatId);
        mentoringSystemFacade.getMessagesByChatId(chatId, numberOfPage);
        return new ResponseEntity<>(OK);
    }
}
