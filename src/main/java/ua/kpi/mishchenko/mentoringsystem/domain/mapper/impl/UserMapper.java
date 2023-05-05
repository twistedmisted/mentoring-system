package ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.Mapper;
import ua.kpi.mishchenko.mentoringsystem.entity.UserEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.RoleRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.RequestUserProjection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class UserMapper implements Mapper<UserEntity, UserDTO> {

    private final RoleRepository roleRepository;
    private final QuestionnaireMapper questionnaireMapper;

    @Override
    public UserEntity dtoToEntity(UserDTO dto) {
        if (isNull(dto)) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setRole(roleRepository.findByName(dto.getRole()).get());
        entity.setQuestionnaire(questionnaireMapper.dtoToEntity(dto.getQuestionnaire()));
        return entity;
    }

    @Override
    public UserDTO entityToDto(UserEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        return UserDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .role(entity.getRole().getName())
                .questionnaire(questionnaireMapper.entityToDto(entity.getQuestionnaire()))
                .build();
    }

    public UserDTO projectionToDto(RequestUserProjection projection) {
        if (isNull(projection)) {
            return null;
        }
        return UserDTO.builder()
                .id(projection.getId())
                .name(projection.getName())
                .surname(projection.getSurname())
                .build();
    }

    @Override
    public List<UserEntity> dtosToEntities(List<UserDTO> dtos) {
        if (isNull(dtos) || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<UserEntity> entities = new ArrayList<>();
        for (UserDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<UserDTO> entitiesToDtos(List<UserEntity> entities) {
        if (isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<UserDTO> dtos = new ArrayList<>();
        for (UserEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }

    public Set<UserDTO> entitiesToDtos(Set<UserEntity> entities) {
        if (isNull(entities) || entities.isEmpty()) {
            return new HashSet<>();
        }
        Set<UserDTO> dtos = new HashSet<>();
        for (UserEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
