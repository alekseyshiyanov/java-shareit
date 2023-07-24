package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class InputBookingDtoJsonTest {
    @Autowired
    private JacksonTester<InputBookingDto> jacksonTester;

    @Test
    void serializeInCorrectFormatWithNanoseconds() throws IOException {
        InputBookingDto testData = new InputBookingDto(
                1L,
                LocalDateTime.of(2023, 5, 9, 16, 1, 10, 123456789),
                LocalDateTime.of(2024, 5, 9, 16, 2, 11, 987654321));

        JsonContent<InputBookingDto> json = jacksonTester.write(testData);

        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2023-05-09T16:01:10.123456789");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2024-05-09T16:02:11.987654321");
    }

    @Test
    void serializeInCorrectFormatWithoutNanoseconds() throws IOException {
        InputBookingDto testData = new InputBookingDto(
                1L,
                LocalDateTime.of(2023, 5, 9, 16, 1, 10),
                LocalDateTime.of(2024, 5, 9, 16, 2, 11));

        JsonContent<InputBookingDto> json = jacksonTester.write(testData);

        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2023-05-09T16:01:10");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2024-05-09T16:02:11");
    }

    @Test
    void deserializeFromCorrectFormatWithNanoseconds() throws IOException {
        String json = "{\"itemId\":1,\"start\":\"2023-05-09T16:01:10.123456789\",\"end\":\"2024-05-09T16:02:11.987654321\"}";

        InputBookingDto testData = jacksonTester.parseObject(json);

        assertThat(testData.getItemId()).isEqualTo(1);
        assertThat(testData.getStart()).isEqualTo(LocalDateTime.of(2023, 5, 9, 16, 1, 10, 123456789));
        assertThat(testData.getEnd()).isEqualTo(LocalDateTime.of(2024, 5, 9, 16, 2, 11, 987654321));
    }

    @Test
    void deserializeFromCorrectFormatWithoutNanoseconds() throws IOException {
        String json = "{\"itemId\":1,\"start\":\"2023-05-09T16:01:10\",\"end\":\"2024-05-09T16:02:11\"}";

        InputBookingDto testData = jacksonTester.parseObject(json);

        assertThat(testData.getItemId()).isEqualTo(1);
        assertThat(testData.getStart()).isEqualTo(LocalDateTime.of(2023, 5, 9, 16, 1, 10));
        assertThat(testData.getEnd()).isEqualTo(LocalDateTime.of(2024, 5, 9, 16, 2, 11));
    }
}
