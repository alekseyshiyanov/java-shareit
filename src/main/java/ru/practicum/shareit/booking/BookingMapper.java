package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toDto(booking.getItem()))
                .booker(UserMapper.toDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> toDto(List<Booking> bookingList) {
        if (bookingList == null) {
            return Collections.emptyList();
        }
        return bookingList.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Booking fromDto(InputBookingDto bookingDto) {
        return Booking.builder()
                .id(null)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(null)
                .booker(null)
                .status(null)
                .build();
    }
}
