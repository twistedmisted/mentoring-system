package ua.kpi.mishchenko.mentoringsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MentoringSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentoringSystemApplication.class, args);
    }

}
