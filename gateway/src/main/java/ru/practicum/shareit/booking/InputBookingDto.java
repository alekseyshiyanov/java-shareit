package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputBookingDto {
    private Long itemId;

    @NotNull(message = "Время начала бронирования не может быть равно null")
    @Future(message = "Время начала бронирования не может быть в прошлом")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования не может быть равно null")
    @Future(message = "Время окончания бронирования не может быть в прошлом")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;
}
