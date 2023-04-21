package ua.kpi.mishchenko.mentoringsystem.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class Util {

    private static final String ZONE_KYIV = "Europe/Kiev";
    private static final ZoneId ZONE_ID_KYIV = ZoneId.of(ZONE_KYIV);

    public static String generateRandomUuid() {
        return UUID.randomUUID().toString();
    }

    public static Timestamp getTimestampNow() {
        return Timestamp.valueOf(LocalDateTime.now(ZONE_ID_KYIV));
    }
}
