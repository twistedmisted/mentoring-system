package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.MessageRequest;
import ua.kpi.mishchenko.mentoringsystem.domain.payload.PrivateMessageResponse;
import ua.kpi.mishchenko.mentoringsystem.facade.ChatSystemFacade;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final ChatSystemFacade chatSystemFacade;

    @MessageMapping("/message/{chatId}")
    public void processMessageSend(@DestinationVariable Long chatId,
                                   @Payload MessageRequest message,
                                   Principal user,
                                   SimpMessageHeaderAccessor headerAccessor) {
        log.debug("Sending message");
        chatSystemFacade.processMessageSend(chatId, message, user.getName(), headerAccessor);
    }

    @GetMapping("/api/v1/messages")
    public ResponseEntity<Map<String, Object>> getMessagesByChat(@RequestParam(value = "chat") Long chatId,
                                                                 @RequestParam(value = "page", required = false, defaultValue = "1")
                                                                 int numberOfPage,
                                                                 Principal principal) {
        log.debug("Getting all messages by chat id = [{}]", chatId);
        PageBO<PrivateMessageResponse> messagePage = chatSystemFacade.getMessagesByChatId(chatId, numberOfPage, principal.getName());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("page", messagePage);
        return new ResponseEntity<>(responseBody, OK);
    }
}
