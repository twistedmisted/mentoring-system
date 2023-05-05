package ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.ChatDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.Mapper;
import ua.kpi.mishchenko.mentoringsystem.entity.ChatEntity;
import ua.kpi.mishchenko.mentoringsystem.entity.MentoringRequestEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.MentoringRequestRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class ChatMapper implements Mapper<ChatEntity, ChatDTO> {

    private final MentoringRequestRepository mentoringRequestRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public ChatEntity dtoToEntity(ChatDTO dto) {
        if (isNull(dto)) {
            return null;
        }
        ChatEntity entity = new ChatEntity();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUsers(userRepository.findAllByIdIn(dto.getUsers().stream().map(UserDTO::getId).toList()));
        entity.addMentoringRequests(mentoringRequestRepository.findAllByIdIn(dto.getMentoringReqIds()));
        return entity;
    }

    @Override
    public ChatDTO entityToDto(ChatEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        ChatDTO dto = new ChatDTO();
        dto.setId(entity.getId());
        dto.setMentoringReqIds(entity.getMentoringRequests().stream().map(MentoringRequestEntity::getId).toList());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUsers(userMapper.entitiesToDtos(entity.getUsers()));
        return dto;
    }

    @Override
    public List<ChatEntity> dtosToEntities(List<ChatDTO> dtos) {
        if (isNull(dtos) || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<ChatEntity> entities = new ArrayList<>();
        for (ChatDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<ChatDTO> entitiesToDtos(List<ChatEntity> entities) {
        if (isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<ChatDTO> dtos = new ArrayList<>();
        for (ChatEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
