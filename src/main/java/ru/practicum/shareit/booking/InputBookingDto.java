package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputBookingDto {
    private Long itemId;

    @FutureOrPresent(message = "Дата начала бронирования не должна быть раньше текущей")
    @NotNull(message = "Дата начала бронирования не должна быть null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;

    @FutureOrPresent(message = "Дата окончания бронирования не должна быть раньше текущей")
    @NotNull(message = "Дата окончания бронирования не должна быть null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;
}
