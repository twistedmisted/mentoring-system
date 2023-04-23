package ua.kpi.mishchenko.mentoringsystem.task;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AliveHostTask {

    private static final String GOOGLE_URL = "https://www.google.com/";
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Scheduled(cron = "0 0/10 * * * *", zone = "Europe/Kiev")
    public void run() {
        REST_TEMPLATE.getForEntity(GOOGLE_URL, String.class);
    }
}
