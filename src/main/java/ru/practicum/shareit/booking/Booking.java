package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private Integer id;

    private LocalDate start;

    private LocalDate end;

    private Integer item;

    private Integer booker;

    private BookingStatus status;
}

