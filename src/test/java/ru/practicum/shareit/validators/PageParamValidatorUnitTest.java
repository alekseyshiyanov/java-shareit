package ru.practicum.shareit.validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PageParamValidatorUnitTest {

    @Test
    void validatePageParamNullPageAndSizeBehavior() {
        var result = PageParamValidator.validate(null, null);

        Assertions.assertFalse(result);
        Assertions.assertEquals("Параметр запроса 'from' не может быть null", PageParamValidator.errorMessage);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, PageParamValidator.httpStatusCode);
    }

    @Test
    void validatePageParamNullPageBehavior() {
        var result = PageParamValidator.validate(null, 20);

        Assertions.assertFalse(result);
        Assertions.assertEquals("Параметр запроса 'from' не может быть null", PageParamValidator.errorMessage);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, PageParamValidator.httpStatusCode);
    }

    @Test
    void validatePageParamNegativePageBehavior() {
        var result = PageParamValidator.validate(-1, 20);

        Assertions.assertFalse(result);
        Assertions.assertEquals("Параметр запроса 'from' не может быть отрицательным", PageParamValidator.errorMessage);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, PageParamValidator.httpStatusCode);
    }

    @Test
    void validatePageParamNullSizeBehavior() {
        var result = PageParamValidator.validate(0, null);

        Assertions.assertFalse(result);
        Assertions.assertEquals("Параметр запроса 'size' не может быть null", PageParamValidator.errorMessage);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, PageParamValidator.httpStatusCode);
    }

    @Test
    void validatePageParamNegativeSizeBehavior() {
        var result = PageParamValidator.validate(0, -1);

        Assertions.assertFalse(result);
        Assertions.assertEquals("Параметр запроса 'size' не может быть отрицательным", PageParamValidator.errorMessage);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, PageParamValidator.httpStatusCode);
    }

    @Test
    void validatePageParamZeroSizeBehavior() {
        var result = PageParamValidator.validate(0, 0);

        Assertions.assertFalse(result);
        Assertions.assertEquals("Параметр запроса 'size' не может быть равен нулю", PageParamValidator.errorMessage);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, PageParamValidator.httpStatusCode);
    }

    @Test
    void validatePageParamStandardBehavior() {
        var result = PageParamValidator.validate(0, 1);

        Assertions.assertTrue(result);
    }
}