package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api/v1/test")
public class DummyController {

    private static final Random RANDOM = new Random();

    @GetMapping
    public int getTests() {
        return RANDOM.nextInt();
    }
}
