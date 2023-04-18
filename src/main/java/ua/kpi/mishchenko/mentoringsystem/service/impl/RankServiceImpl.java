package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kpi.mishchenko.mentoringsystem.repository.RankRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.RankNameOnly;
import ua.kpi.mishchenko.mentoringsystem.service.RankService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final RankRepository rankRepository;

    @Override
    public List<String> getAllRankNames() {
        log.debug("Getting all rank names");
        List<RankNameOnly> ranks = rankRepository.findProjections();
        if (ranks.isEmpty()) {
            log.warn("Rank names list is empty");
            return new ArrayList<>();
        }
        log.debug("Rank names list was successfully found");
        return ranks.stream()
                .map(RankNameOnly::getName)
                .toList();
    }
}
