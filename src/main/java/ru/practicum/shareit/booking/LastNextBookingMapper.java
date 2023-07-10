package ru.practicum.shareit.booking;

public class LastNextBookingMapper {
    public static LastNextBookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return LastNextBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}