package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.util.ChatStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class ChatDTO {

    private Long id;
    private List<Long> mentoringReqIds = new ArrayList<>();
    private ChatStatus status;
    private Timestamp createdAt;
    private Set<UserDTO> users;

    public void addMentoringReqId(Long id) {
        mentoringReqIds.add(id);
    }
}
