package ru.practicum.shareit.builders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorMessageTest {

    @Test
    void standardBehaviorTest() {
        var testErrMsg = ErrorMessage.buildRestApiErrorResponse(HttpStatus.NOT_FOUND, "Тестовое сообщение");

        Assertions.assertEquals(String.valueOf(HttpStatus.NOT_FOUND.value()), testErrMsg.get("status code"));
        Assertions.assertEquals(String.valueOf(HttpStatus.NOT_FOUND.getReasonPhrase()), testErrMsg.get("error message"));
        Assertions.assertEquals("Тестовое сообщение", testErrMsg.get("error"));
    }
}