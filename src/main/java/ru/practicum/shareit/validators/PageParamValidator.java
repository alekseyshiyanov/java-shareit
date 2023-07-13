package ru.practicum.shareit.validators;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
public class PageParamValidator {

    public static String errorMessage;
    public static HttpStatus httpStatusCode;

    public static Boolean validate(Integer from, Integer size) {
        if (from == null) {
            httpStatusCode = HttpStatus.BAD_REQUEST;
            errorMessage = "Параметр запроса 'from' не может быть null";
            return false;
        } else {
            if (from < 0) {
                httpStatusCode = HttpStatus.BAD_REQUEST;
                errorMessage = "Параметр запроса 'from' не может быть отрицательным";
                return false;
            }
        }

        if (size == null) {
            httpStatusCode = HttpStatus.BAD_REQUEST;
            errorMessage = "Параметр запроса 'size' не может быть null";
            return false;
        } else {
            if (size < 0) {
                httpStatusCode = HttpStatus.BAD_REQUEST;
                errorMessage = "Параметр запроса 'size' не может быть отрицательным";
                return false;
            }
            if (size == 0) {
                httpStatusCode = HttpStatus.BAD_REQUEST;
                errorMessage = "Параметр запроса 'size' не может быть равен нулю";
                return false;
            }
        }

        return true;
    }
}
