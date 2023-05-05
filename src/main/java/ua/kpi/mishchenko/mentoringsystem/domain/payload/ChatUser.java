package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.Builder;
import lombok.Data;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.UserDTO;
import ua.kpi.mishchenko.mentoringsystem.repository.projection.ChatUserProjection;

@Data
@Builder
public class ChatUser {

    private Long id;
    private String name;
    private String surname;

    public static ChatUser valueOf(UserDTO user) {
        return ChatUser.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .build();
    }

    public static ChatUser valueOf(ChatUserProjection user) {
        return ChatUser.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .build();
    }
}
