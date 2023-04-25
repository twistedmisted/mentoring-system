package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.QuestionnaireDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.QuestionnaireMapper;
import ua.kpi.mishchenko.mentoringsystem.repository.QuestionnaireRepository;
import ua.kpi.mishchenko.mentoringsystem.service.QuestionnaireService;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionnaireMapper questionnaireMapper;

    @Override
    public void updateQuestionnaire(QuestionnaireDTO questionnaireDto) {
        log.debug("Updating questionnaire by user id = [{}]", questionnaireDto.getUserId());
        questionnaireRepository.save(questionnaireMapper.dtoToEntity(questionnaireDto));
    }
}
