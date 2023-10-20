package org.nurma.aqyndar.controller;

import lombok.extern.log4j.Log4j2;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.response.CustomErrorResponse;
import org.nurma.aqyndar.exception.CustomAuthenticationException;
import org.nurma.aqyndar.exception.ResourceNotFound;
import org.nurma.aqyndar.exception.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class ExceptionHandlerController {

    private ResponseEntity<Object> buildResponse(final String title, final String message, final HttpStatus status) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(title, message);
        return new ResponseEntity<>(customErrorResponse, status);
    }

    @ExceptionHandler(value = {CustomAuthenticationException.class})
    public ResponseEntity<Object> handleAuthException(final CustomAuthenticationException e) {
        return buildResponse(ExceptionTitle.AUTHENTICATION, e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity<Object> handleValidationException(final ValidationException e) {
        return buildResponse(ExceptionTitle.VALIDATION, e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ResourceNotFound.class})
    public ResponseEntity<Object> handleResourceNotFoundException(final ResourceNotFound e) {
        return buildResponse(ExceptionTitle.NOT_FOUND, e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleValidationException(final MethodArgumentNotValidException e) {
        return buildResponse(ExceptionTitle.VALIDATION, e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<Object> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        return buildResponse(ExceptionTitle.VALIDATION, e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("{} caught, message: {}", e.getClass(), e.getMessage());
        return buildResponse(ExceptionTitle.VALIDATION, e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(final Exception e) {
        log.error("!!!!!Not caught exception!!!!!");
        log.error("Stack trace: ", e);
        return buildResponse(ExceptionTitle.UNCAUGHT, "Something went wrong. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
