package ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MessageDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.Mapper;
import ua.kpi.mishchenko.mentoringsystem.entity.MessageEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.ChatRepository;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class MessageMapper implements Mapper<MessageEntity, MessageDTO> {

    private final ChatRepository chatRepository;
    private final UserMapper userMapper;

    @Override
    public MessageEntity dtoToEntity(MessageDTO dto) {
        if (isNull(dto)) {
            return null;
        }
        MessageEntity entity = new MessageEntity();
        entity.setId(dto.getId());
        entity.setText(dto.getText());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setFromUser(userMapper.dtoToEntity(dto.getFromUser()));
        entity.setChat(chatRepository.findById(dto.getChatId()).get());
        return entity;
    }

    @Override
    public MessageDTO entityToDto(MessageEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        MessageDTO dto = new MessageDTO();
        dto.setId(entity.getId());
        dto.setText(entity.getText());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setFromUser(userMapper.entityToDto(entity.getFromUser()));
        dto.setChatId(entity.getChat().getId());
        return dto;
    }

    @Override
    public List<MessageEntity> dtosToEntities(List<MessageDTO> dtos) {
        if (isNull(dtos) || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<MessageEntity> entities = new ArrayList<>();
        for (MessageDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<MessageDTO> entitiesToDtos(List<MessageEntity> entities) {
        if (isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<MessageDTO> dtos = new ArrayList<>();
        for (MessageEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
