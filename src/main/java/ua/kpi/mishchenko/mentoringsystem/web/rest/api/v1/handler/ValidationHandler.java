package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j
public class ValidationHandler extends ResponseEntityExceptionHandler {

    private static final String KEY_DETAIL = "detail";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {

            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(KEY_DETAIL, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        String message = e.getMessage();
        responseBody.put(KEY_DETAIL, message.substring(message.indexOf(":") + 1));
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(KEY_DETAIL, "Необхідно авторизуватися, щоб отримати цю інформацію.");
        return new ResponseEntity<>(responseBody, FORBIDDEN);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.info("{}", ex.getCause());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(KEY_DETAIL, "Схоже ми маємо деякі проблеми. Спробуйте відправити запит повторно або зачекати. Можливо ми вже вирішуємо цю проблему.");
        return new ResponseEntity<>(responseBody, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
