package ua.kpi.mishchenko.mentoringsystem.domain.dto;

import lombok.Data;

import java.io.InputStream;

@Data
public class MediaDTO {

    private String filename; // is user id
    private InputStream inputStream;
}
