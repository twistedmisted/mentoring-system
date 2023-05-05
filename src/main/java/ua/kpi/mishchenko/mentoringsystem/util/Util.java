package ua.kpi.mishchenko.mentoringsystem.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class Util {

    private static final String ZONE_KYIV = "Europe/Kiev";
    private static final ZoneId ZONE_ID_KYIV = ZoneId.of(ZONE_KYIV);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static final DateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static String generateRandomUuid() {
        return UUID.randomUUID().toString();
    }

    public static Timestamp getTimestampNow() {
        return Timestamp.valueOf(LocalDateTime.now(ZONE_ID_KYIV));
    }

    public static boolean lessThanOne(int numberOfPage) {
        return numberOfPage < 1;
    }

    public static String parseTimestampToStringDate(Timestamp timestamp) {
        return DATE_FORMAT.format(timestamp);
    }

    public static String parseTimestampToISO8601String(Timestamp timestamp) {
        return ISO_DATE_FORMAT.format(timestamp);
    }

    public static Timestamp ISOStringToTimestamp(String isoDate) {
        try {
            return new Timestamp(ISO_DATE_FORMAT.parse(isoDate).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Cannot parse isoDate to Timestamp");
        }
    }
}
