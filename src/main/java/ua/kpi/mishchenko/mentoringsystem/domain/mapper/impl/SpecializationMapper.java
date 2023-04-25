package ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl;

import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.SpecializationDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.Mapper;
import ua.kpi.mishchenko.mentoringsystem.entity.SpecializationEntity;

import java.util.List;

import static java.util.Objects.isNull;

@Component
public class SpecializationMapper implements Mapper<SpecializationEntity, SpecializationDTO> {

    @Override
    public SpecializationEntity dtoToEntity(SpecializationDTO dto) {
        if (isNull(dto)) {
            return null;
        }
        SpecializationEntity entity = new SpecializationEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }

    @Override
    public SpecializationDTO entityToDto(SpecializationEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        SpecializationDTO dto = new SpecializationDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    public List<SpecializationEntity> dtosToEntities(List<SpecializationDTO> dtos) {
        throw new UnsupportedOperationException("Need to implement 'dtosToEntities' method in SpecializationMapper");
    }

    @Override
    public List<SpecializationDTO> entitiesToDtos(List<SpecializationEntity> entities) {
        throw new UnsupportedOperationException("Need to implement 'entitiesToDtos' method in SpecializationMapper");
    }
}
