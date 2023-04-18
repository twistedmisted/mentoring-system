package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kpi.mishchenko.mentoringsystem.repository.SpecializationRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.SpecializationNameOnly;
import ua.kpi.mishchenko.mentoringsystem.service.SpecializationService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecializationServiceImpl implements SpecializationService {

    private final SpecializationRepository specializationRepository;

    @Override
    public List<String> getAllSpecializationNames() {
        log.debug("Getting all specialization names");
        List<SpecializationNameOnly> ranks = specializationRepository.findProjections();
        if (ranks.isEmpty()) {
            log.warn("Specialization names list is empty");
            return new ArrayList<>();
        }
        log.debug("Specialization names list was successfully found");
        return ranks.stream()
                .map(SpecializationNameOnly::getName)
                .toList();
    }
}
