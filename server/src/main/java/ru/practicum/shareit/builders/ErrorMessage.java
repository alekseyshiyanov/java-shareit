package ru.practicum.shareit.builders;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ErrorMessage {

    public static Map<String, String> buildRestApiErrorResponse(HttpStatus httpStatus, String msg) {
        Map<String, String> errorMsg = new HashMap<>();

        errorMsg.put("timestamp", LocalDateTime.now().toString());
        errorMsg.put("status code", String.valueOf(httpStatus.value()));
        errorMsg.put("error message", httpStatus.getReasonPhrase());
        errorMsg.put("error", msg);

        return errorMsg;
    }
}
