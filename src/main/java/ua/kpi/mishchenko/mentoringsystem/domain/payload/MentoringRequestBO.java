package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MentoringRequestBO {

    @NotNull(message = "Ідентифікатор користувача, якому надсилається запит, не вказано.")
    private Long toUserId;
}
