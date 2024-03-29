package ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MentoringRequestDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.Mapper;
import ua.kpi.mishchenko.mentoringsystem.entity.MentoringRequestEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.MentoringRequestProjection;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class MentoringRequestMapper implements Mapper<MentoringRequestEntity, MentoringRequestDTO> {

    private final UserMapper userMapper;

    @Override
    public MentoringRequestEntity dtoToEntity(MentoringRequestDTO dto) {
        if (isNull(dto)) {
            return null;
        }
        MentoringRequestEntity entity = new MentoringRequestEntity();
        entity.setId(dto.getId());
        entity.setFrom(userMapper.dtoToEntity(dto.getFrom()));
        entity.setTo(userMapper.dtoToEntity(dto.getTo()));
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    @Override
    public MentoringRequestDTO entityToDto(MentoringRequestEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        return MentoringRequestDTO.builder()
                .id(entity.getId())
                .from(userMapper.entityToDto(entity.getFrom()))
                .to(userMapper.entityToDto(entity.getTo()))
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public MentoringRequestDTO projectionToDto(MentoringRequestProjection projection) {
        if (isNull(projection)) {
            return null;
        }
        return MentoringRequestDTO.builder()
                .id(projection.getId())
                .from(userMapper.projectionToDto(projection.getFromUser()))
                .status(projection.getStatus())
                .build();
    }

    @Override
    public List<MentoringRequestEntity> dtosToEntities(List<MentoringRequestDTO> dtos) {
        if (isNull(dtos) || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<MentoringRequestEntity> entities = new ArrayList<>();
        for (MentoringRequestDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<MentoringRequestDTO> entitiesToDtos(List<MentoringRequestEntity> entities) {
        if (isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<MentoringRequestDTO> dtos = new ArrayList<>();
        for (MentoringRequestEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
