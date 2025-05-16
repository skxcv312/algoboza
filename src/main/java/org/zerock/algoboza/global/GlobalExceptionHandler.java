package org.zerock.algoboza.global;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {
    private void logError(String message, Throwable ex) {
        if (ex != null) {
            log.error("Error: {} - Exception: {} - StackTrace: ", message, ex.getMessage(), ex);
        } else {
            log.error("Error: {}", message);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Response<?> handleEmailNotFound(IllegalArgumentException ex) {
        logError("IllegalArgumentException occurred", ex);
        return Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public Response<?> handleGeneral(Exception ex) {
        logError("Unhandled exception occurred", ex);
        return Response.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(NullPointerException.class)
    public Response<?> handleNullPointer(NullPointerException ex) {
        logError("NullPointerException occurred", ex);
        return Response.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    public Response<?> handleGeneral(RuntimeException ex) {
        logError("RuntimeException occurred", ex);
        return Response.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Response<?> handleExpiredJwtException(ExpiredJwtException ex) {
        logError("ExpiredJwtException occurred", ex);
        return Response.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(JwtException.class)
    public Response<?> handleJwtException(JwtException ex) {
        logError("JwtException occurred", ex);
        return Response.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();
    }


}
