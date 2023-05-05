package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.domain.util.ChatStatus;
import ua.kpi.mishchenko.mentoringsystem.entity.ChatEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.PrivateChat;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends CrudRepository<ChatEntity, Long> {

    @Query("SELECT c.id AS id, concat(u.surname, ' ', u.name) AS title, " +
            "coalesce(m.createdAt, c.createdAt) AS lastMessageCreatedAt, " +
            "u.id AS toUserId, coalesce(m.text, '') AS lastMessageText, " +
            "(SELECT count(tm) FROM MessageEntity tm WHERE tm.status = ua.kpi.mishchenko.mentoringsystem.domain.util.MessageStatus.UNSENT AND tm.chat.id = :id AND tm.fromUser.email != :reqEmail) AS unreadMessages, " +
            "c.status AS status " +
            "FROM ChatEntity c " +
            "JOIN c.users u " +
            "LEFT JOIN c.messages m " +
            "WHERE c.id = :id AND u.email != :reqEmail ORDER BY m.createdAt DESC " +
            "LIMIT 1")
    Optional<PrivateChat> findProjections(Long id, String reqEmail);

    @Query("SELECT c.id AS id, " +
            "       CONCAT(u.surname, ' ', u.name) AS title, " +
            "       COALESCE(m.createdAt, c.createdAt) AS lastMessageCreatedAt, " +
            "       COALESCE(m.text, '') AS lastMessageText, " +
            "       u.id AS toUserId, " +
            "       COUNT(m2) AS unreadMessages," +
            "       c.status AS status " +
            "FROM ChatEntity c " +
            "JOIN c.users u " +
            "LEFT JOIN c.messages m ON m.createdAt = (" +
            "    SELECT MAX(message.createdAt) " +
            "    FROM c.messages message " +
            "    WHERE message.chat.id = c.id " +
            ") AND m.id = (" +
            "    SELECT MAX(message.id) " +
            "    FROM c.messages message " +
            "    WHERE message.chat.id = c.id " +
            ")" +
            "LEFT JOIN c.messages m2 " +
            "ON m2.status = ua.kpi.mishchenko.mentoringsystem.domain.util.MessageStatus.UNSENT " +
            "AND m2.fromUser.email != :userEmail " +
            "WHERE c.id IN ( " +
            "    SELECT c.id " +
            "    FROM ChatEntity c " +
            "    JOIN c.users u " +
            "    WHERE u.email = :userEmail " +
            ") " +
            "AND u.email != :userEmail " +
            "GROUP BY c.id, u.surname, u.name, m.createdAt, c.createdAt, m.text, u.id ")
    Page<PrivateChat> findAllProjections(String userEmail, Pageable pageable);

    Optional<ChatEntity> findByUsersIdIn(List<Long> ids);

    boolean existsByIdAndUsersEmail(Long id, String email);

    boolean existsByIdAndStatus(Long chatId, ChatStatus status);

    @Modifying
    @Query("UPDATE ChatEntity c " +
            "SET c.status = ua.kpi.mishchenko.mentoringsystem.domain.util.ChatStatus.ARCHIVED " +
            "WHERE c.id = (SELECT m.chat.id FROM MentoringRequestEntity m WHERE m.id = :reqId) ")
    void updateChatStatusByMentoringReqId(Long reqId);
}
