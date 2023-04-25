package ua.kpi.mishchenko.mentoringsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.QuestionnaireDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl.QuestionnaireMapper;
import ua.kpi.mishchenko.mentoringsystem.entity.QuestionnaireEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.QuestionnaireRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.RankRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.SpecializationRepository;
import ua.kpi.mishchenko.mentoringsystem.service.QuestionnaireService;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final RankRepository rankRepository;
    private final SpecializationRepository specializationRepository;
    private final QuestionnaireMapper questionnaireMapper;

    @Override
    public void updateQuestionnaire(QuestionnaireDTO questionnaireDto) {
        Long userId = questionnaireDto.getUserId();
        log.debug("Updating questionnaire by user id = [{}]", userId);
        QuestionnaireEntity questionnaireEntity = new QuestionnaireEntity();
        if (existsById(userId)) {
            questionnaireEntity = questionnaireRepository.findById(userId).get();
            mapNewToOldEntity(questionnaireEntity, questionnaireDto);
        } else {
            questionnaireEntity = questionnaireMapper.dtoToEntity(questionnaireDto);
        }
        questionnaireRepository.save(questionnaireEntity);
    }

    private void mapNewToOldEntity(QuestionnaireEntity questionnaireEntity, QuestionnaireDTO questionnaireDto) {
        questionnaireEntity.setAbout(questionnaireDto.getAbout());
        questionnaireEntity.setRank(rankRepository.findByName(questionnaireDto.getRank()).get());
        questionnaireEntity.setSpecialization(specializationRepository.findByName(questionnaireDto.getSpecialization()).get());
        questionnaireEntity.setSkills(questionnaireDto.getSkills());
        questionnaireEntity.setCompanies(questionnaireDto.getCompanies());
        questionnaireEntity.setLinkedin(questionnaireDto.getLinkedin());
        questionnaireEntity.setHoursPerWeek(questionnaireDto.getHoursPerWeek());
    }

    private boolean existsById(Long userId) {
        return questionnaireRepository.existsById(userId);
    }
}
