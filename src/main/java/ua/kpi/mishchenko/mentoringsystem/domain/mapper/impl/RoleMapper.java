package ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl;

import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.RoleDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.RoleEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.Mapper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Component
public class RoleMapper implements Mapper<RoleEntity, RoleDTO> {

    @Override
    public RoleEntity dtoToEntity(RoleDTO dto) {
        if (isNull(dto)) {
            return null;
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }

    @Override
    public RoleDTO entityToDto(RoleEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    public List<RoleEntity> dtosToEntities(List<RoleDTO> dtos) {
        if (isNull(dtos) || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<RoleEntity> entities = new ArrayList<>();
        for (RoleDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<RoleDTO> entitiesToDtos(List<RoleEntity> entities) {
        if (isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<RoleDTO> dtos = new ArrayList<>();
        for (RoleEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
