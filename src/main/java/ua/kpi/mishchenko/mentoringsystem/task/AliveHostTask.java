package ua.kpi.mishchenko.mentoringsystem.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AliveHostTask {

    private static final String URL = "https://attenbot.onrender.com/api/users";

    @Scheduled(fixedDelay = 12 * 60 * 1000)
    public void run() {
        RestTemplate restTemplate = new RestTemplate();
        Integer users = restTemplate.getForObject(URL, Integer.class);
    }
}
