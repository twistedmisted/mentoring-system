package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    private Long toUserId;
    private String text;
    private String createdAt;
}
