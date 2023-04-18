package ua.kpi.mishchenko.mentoringsystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s3")
@Getter
@Setter
public class S3Config {

    private String accessKey;
    private String secretKey;
}
