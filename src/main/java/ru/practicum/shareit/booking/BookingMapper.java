package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

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
