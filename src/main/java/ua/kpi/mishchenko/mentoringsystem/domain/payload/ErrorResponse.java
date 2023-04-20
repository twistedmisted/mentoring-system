package ua.kpi.mishchenko.mentoringsystem.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private Integer status;
//    private String title;
    private String message;
}
