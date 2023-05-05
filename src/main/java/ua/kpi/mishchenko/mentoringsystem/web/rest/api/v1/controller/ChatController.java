package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.ChatBO;
import ua.kpi.mishchenko.mentoringsystem.domain.bo.PageBO;
import ua.kpi.mishchenko.mentoringsystem.facade.ChatSystemFacade;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatSystemFacade chatSystemFacade;

    @GetMapping("/{chatId}")
    public ResponseEntity<Map<String, Object>> getChatById(@PathVariable Long chatId,
                                                           Principal principal) {
        log.debug("Getting chat by id = [{}]", chatId);
        ChatBO chat = chatSystemFacade.getChatById(chatId, principal.getName());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("chat", chat);
        return new ResponseEntity<>(responseBody, OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getChatsForUser(@RequestParam(value = "page", required = false, defaultValue = "1")
                                                               int numberOfPage,
                                                               Principal principal) {
        log.debug("Getting chats for user with email = [{}]", principal.getName());
        PageBO<ChatBO> chatPage = chatSystemFacade.getChatsByUserEmail(principal.getName(), numberOfPage);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("page", chatPage);
        return new ResponseEntity<>(responseBody, OK);
    }
}
