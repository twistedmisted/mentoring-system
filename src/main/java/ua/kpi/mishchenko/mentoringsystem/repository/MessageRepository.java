package ua.kpi.mishchenko.mentoringsystem.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MessageStatus;
import ua.kpi.mishchenko.mentoringsystem.entity.MessageEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.PrivateMessage;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<MessageEntity, Long> {

    @Query("SELECT m.id AS id, " +
            "m.text AS text, " +
            "m.createdAt AS createdAt, " +
            "m.fromUser AS chatUser, " +
            "m.status AS status " +
            "FROM MessageEntity m " +
            "WHERE m.chat.id = :chatId")
    Page<PrivateMessage> findAllProjections(Long chatId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE MessageEntity m SET m.status = :status WHERE m.id = :messageId")
    void updateMessageStatus(Long messageId, MessageStatus status);

    @Transactional
    @Modifying
    @Query("UPDATE MessageEntity m SET m.status = :status WHERE m.id IN :messageIds")
    void updateMessagesStatus(List<Long> messageIds, MessageStatus status);

    @Query("SELECT COUNT(m) > 0 " +
            "FROM ChatEntity c" +
            "         JOIN c.users u " +
            "         JOIN c.messages m " +
            "WHERE u.email = :userEmail AND m.fromUser.email != :userEmail AND m.status = ua.kpi.mishchenko.mentoringsystem.domain.util.MessageStatus.UNSENT ")
    boolean existsUnreadMessageByUserEmail(String userEmail);
}
