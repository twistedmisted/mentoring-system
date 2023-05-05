package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestUser {

    private Long id;
    private String name;
    private String surname;

    @JsonProperty("avatar")
    private String profilePhotoUrl;
}
