package ua.kpi.mishchenko.mentoringsystem.domain.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.ReviewDTO;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.ReviewEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.mapper.Mapper;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class ReviewMapper implements Mapper<ReviewEntity, ReviewDTO> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public ReviewEntity dtoToEntity(ReviewDTO dto) {
        if (isNull(dto)) {
            return null;
        }
        ReviewEntity entity = new ReviewEntity();
        entity.setId(dto.getId());
        entity.setText(dto.getText());
        entity.setRating(dto.getRating());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setToUser(userRepository.findById(dto.getToUser().getId()).get());
        entity.setFromUser(userRepository.findById(dto.getFromUser().getId()).get());
        return entity;
    }

    @Override
    public ReviewDTO entityToDto(ReviewEntity entity) {
        if (isNull(entity)) {
            return null;
        }
        ReviewDTO dto = new ReviewDTO();
        dto.setId(entity.getId());
        dto.setText(entity.getText());
        dto.setRating(entity.getRating());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setToUser(userMapper.entityToDto(entity.getToUser()));
        dto.setFromUser(userMapper.entityToDto(entity.getFromUser()));
        return dto;
    }

    @Override
    public List<ReviewEntity> dtosToEntities(List<ReviewDTO> dtos) {
        if (isNull(dtos) || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<ReviewEntity> entities = new ArrayList<>();
        for (ReviewDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<ReviewDTO> entitiesToDtos(List<ReviewEntity> entities) {
        if (isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<ReviewDTO> dtos = new ArrayList<>();
        for (ReviewEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
