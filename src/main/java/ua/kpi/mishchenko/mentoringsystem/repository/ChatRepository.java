package ua.kpi.mishchenko.mentoringsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mishchenko.mentoringsystem.entity.ChatEntity;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.PrivateChat;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends CrudRepository<ChatEntity, Long> {

    @Query("SELECT c.id AS id, concat(u.surname, ' ', u.name) AS title, " +
            "coalesce(m.createdAt, c.createdAt) AS lastMessageCreatedAt, " +
            "u.id AS toUserId, coalesce(m.text, '') AS lastMessageText " +
            "FROM ChatEntity c " +
            "JOIN c.users u " +
            "LEFT JOIN c.messages m " +
            "WHERE c.id = :id AND u.email != :reqEmail ORDER BY m.createdAt DESC " +
            "LIMIT 1")
    Optional<PrivateChat> findProjections(Long id, String reqEmail);

    @Query("SELECT c.id AS id, \n" +
            "       CONCAT(u.surname, ' ', u.name) AS title, \n" +
            "       COALESCE(m.createdAt, c.createdAt) AS lastMessageCreatedAt, \n" +
            "       COALESCE(m.text, '') AS lastMessageText, \n" +
            "       u.id AS toUserId \n" +
            "FROM ChatEntity c \n" +
            "JOIN c.users u \n" +
            "LEFT JOIN c.messages m ON m.createdAt = (\n" +
            "    SELECT MAX(message.createdAt) \n" +
            "    FROM c.messages message \n" +
            ") \n" +
            "WHERE c.id IN (\n" +
            "    SELECT c.id \n" +
            "    FROM ChatEntity c \n" +
            "    JOIN c.users u \n" +
            "    WHERE u.email = :userEmail\n" +
            ") \n" +
            "AND u.email != :userEmail \n")
    Page<PrivateChat> findAllProjections(String userEmail, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(DISTINCT u.id) = 2 THEN true ELSE false END FROM ChatEntity c " +
            "JOIN c.users u WHERE u.id IN :ids GROUP BY c.id")
    Optional<Boolean> existsChatWithUsers(List<Long> ids);


    boolean existsByIdAndUsersEmail(Long id, String email);
}
