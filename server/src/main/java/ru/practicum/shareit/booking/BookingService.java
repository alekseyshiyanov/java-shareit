package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(InputBookingDto bookingDto, Long bookerId);

    BookingDto approvalBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingDto getBookingByUser(Long bookingId, Long userId);

    List<BookingDto> getAllBookingByUser(Long userId, Integer from, Integer size, String state);

    List<BookingDto> getAllBookingByOwner(Long ownerId, Integer from, Integer size, String state);
}
