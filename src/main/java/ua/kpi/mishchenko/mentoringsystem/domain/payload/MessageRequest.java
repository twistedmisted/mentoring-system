package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    private String id;

    @NotBlank(message = "Message text can't be empty.")
    private String text;

    @NotBlank(message = "Message date can't be empty.")
    @JsonProperty("date")
    private String createdAt;
}
