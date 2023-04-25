package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.entity.MessageEntity;

@Repository
public interface MessageRepository extends CrudRepository<MessageEntity, Long> {
}