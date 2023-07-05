package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class ApiErrorException extends RuntimeException {
    private final HttpStatus httpStatusCode;
    public ApiErrorException(HttpStatus httpStatusCode, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatus getStatusCode() {
        return httpStatusCode;
    }
}