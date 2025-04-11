package org.zerock.arcteryx.global;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public Response<?> handleEmailNotFound(IllegalArgumentException ex) {
        return Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .data("IllegalArgumentException")
                .massage(ex.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public Response<?> handleGeneral(Exception ex) {
        return Response.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .data("Exception")
                .massage(ex.getMessage())
                .build();
    }

    @ExceptionHandler(NullPointerException.class)
    public Response<?> handleNullPointer(NullPointerException ex) {
        return Response.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .data("NullPointerException")
                .massage(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Response<?> handleExpiredJwtException(ExpiredJwtException ex) {
        return Response.builder()
                .status(HttpStatus.FORBIDDEN)
                .data("ExpiredJwtException")
                .massage(ex.getMessage())
                .build();
    }

    @ExceptionHandler(JwtException.class)
    public Response<?> handleJwtException(JwtException ex) {
        return Response.builder()
                .status(HttpStatus.FORBIDDEN)
                .massage(ex.getMessage())
                .data("JwtException")
                .build();
    }


}