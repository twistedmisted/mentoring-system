package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class RoleDTO {

    @JsonIgnore
    private Integer id;
    private String name;
}
