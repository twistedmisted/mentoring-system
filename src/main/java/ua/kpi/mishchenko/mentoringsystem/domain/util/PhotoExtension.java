package ua.kpi.mishchenko.mentoringsystem.domain.util;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

public enum PhotoExtension {
    JPEG(IMAGE_JPEG_VALUE),
    PNG(IMAGE_PNG_VALUE);

    private static final PhotoExtension[] PHOTO_EXTENSIONS = values();
    private final String extension;

    PhotoExtension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static boolean exists(String extension) {
        for (PhotoExtension photoExtension : PHOTO_EXTENSIONS) {
            if (photoExtension.getExtension().equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
