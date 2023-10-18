package org.nurma.aqyndar.controller;

import org.nurma.aqyndar.dto.response.CustomErrorResponse;
import org.nurma.aqyndar.exception.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<Object> handleAuthException(final AuthenticationException e) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(
                "Authentication failed",
                e.getMessage()
        );

        return new ResponseEntity<>(customErrorResponse, HttpStatus.UNAUTHORIZED);
    }
}
