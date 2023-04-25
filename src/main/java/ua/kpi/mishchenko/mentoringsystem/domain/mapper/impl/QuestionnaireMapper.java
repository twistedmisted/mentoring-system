package ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.QuestionnaireDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.Mapper;
import ua.kpi.mishchenko.mentoringsystem.entity.QuestionnaireEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.RankRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.SpecializationRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class QuestionnaireMapper implements Mapper<QuestionnaireEntity, QuestionnaireDTO> {

    private final UserRepository userRepository;
    private final RankRepository rankRepository;
    private final SpecializationRepository specializationRepository;

    @Override
    public QuestionnaireEntity dtoToEntity(QuestionnaireDTO dto) {
        if (isNull(dto)) {
            return null;
        }
        QuestionnaireEntity entity = new QuestionnaireEntity();
        entity.setUserId(dto.getUserId());
        entity.setAbout(dto.getAbout());
        entity.setSkills(dto.getSkills());
        entity.setCompanies(dto.getCompanies());
        entity.setRank(rankRepository.findByName(dto.getRank()).get());
        entity.setSpecialization(specializationRepository.findByName(dto.getSpecialization()).get());
        entity.setLinkedin(dto.getLinkedin());
        entity.setHoursPerWeek(dto.getHoursPerWeek());
        entity.setUser(userRepository.findById(dto.getUserId()).get());
        return entity;
    }

    @Override
    public QuestionnaireDTO entityToDto(QuestionnaireEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        QuestionnaireDTO dto = new QuestionnaireDTO();
        dto.setUserId(entity.getUserId());
        dto.setAbout(entity.getAbout());
        dto.setSkills(entity.getSkills());
        dto.setCompanies(entity.getCompanies());
        dto.setRank(entity.getRank().getName());
        dto.setSpecialization(entity.getSpecialization().getName());
        dto.setLinkedin(entity.getLinkedin());
        dto.setHoursPerWeek(entity.getHoursPerWeek());
        return dto;
    }

    @Override
    public List<QuestionnaireEntity> dtosToEntities(List<QuestionnaireDTO> dtos) {
        if (isNull(dtos) || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<QuestionnaireEntity> entities = new ArrayList<>();
        for (QuestionnaireDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<QuestionnaireDTO> entitiesToDtos(List<QuestionnaireEntity> entities) {
        if (isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<QuestionnaireDTO> dtos = new ArrayList<>();
        for (QuestionnaireEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
