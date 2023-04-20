package ua.kpi.mishchenko.mentoringsystem.exception;

public class IllegalPhotoExtensionException extends RuntimeException {

    public IllegalPhotoExtensionException() {
    }

    public IllegalPhotoExtensionException(String message) {
        super(message);
    }

    public IllegalPhotoExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
