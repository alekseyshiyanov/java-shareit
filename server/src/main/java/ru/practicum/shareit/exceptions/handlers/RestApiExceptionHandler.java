package ru.practicum.shareit.exceptions.handlers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.builders.ErrorMessage;
import ru.practicum.shareit.exceptions.ApiErrorException;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(ApiErrorException.class)
    public ResponseEntity<Object> handleApiErrorException(ApiErrorException e) {
        var errMsg = ErrorMessage.buildRestApiErrorResponse(e.getStatusCode(), e.getMessage());
        return new ResponseEntity<>(errMsg, new HttpHeaders(), e.getStatusCode());
    }
}
