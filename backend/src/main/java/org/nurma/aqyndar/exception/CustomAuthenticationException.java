package org.nurma.aqyndar.exception;

public class CustomAuthenticationException extends RuntimeException {
    public CustomAuthenticationException(final String message) {
        super(message);
    }
}
