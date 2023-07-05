package ru.practicum.shareit.booking;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface BookingService {

    BookingDto createBooking(InputBookingDto bookingDto, Long bookerId);

    BookingDto approvalBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingDto getBookingByUser(Long bookingId, Long userId);

    List<BookingDto> getAllBookingByUser(Long userId, String state);

    List<BookingDto> getAllBookingByOwner(Long ownerId, String state);
}
