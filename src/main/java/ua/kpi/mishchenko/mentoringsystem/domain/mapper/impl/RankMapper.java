package ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl;

import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.RankDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.RankEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.Mapper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Component
public class RankMapper implements Mapper<RankEntity, RankDTO> {

    @Override
    public RankEntity dtoToEntity(RankDTO dto) {
        if (isNull(dto)) {
            return null;
        }
        RankEntity entity = new RankEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }

    @Override
    public RankDTO entityToDto(RankEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        RankDTO dto = new RankDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    public List<RankEntity> dtosToEntities(List<RankDTO> dtos) {
        throw new UnsupportedOperationException("Need to implement 'dtosToEntities' method in RankMapper");
    }

    @Override
    public List<RankDTO> entitiesToDtos(List<RankEntity> entities) {
        if (isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<RankDTO> dtos = new ArrayList<>();
        for (RankEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
