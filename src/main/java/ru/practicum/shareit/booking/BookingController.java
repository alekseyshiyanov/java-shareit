package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody InputBookingDto bookingDto,
                                    @RequestHeader(value = "X-Sharer-User-Id", required = false) Long bookerId) {
        log.info("Запрос на создание новой записи");
        return bookingService.createBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvalBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                                      @PathVariable("bookingId") Long bookingId,
                                      @RequestParam("approved") Boolean approved) {
        log.info("Запрос на смену статуса бронирования с ID={} от пользователя с ID={}", bookingId, ownerId);
        return bookingService.approvalBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingByUser(@PathVariable("bookingId") Long bookingId,
                                       @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Запрос на получение данных бронирования с ID={} пользователем с ID={}", bookingId, userId);
        return bookingService.getBookingByUser(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingByUser(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                @RequestParam(value = "from", required = false) Integer from,
                                                @RequestParam(value = "size", required = false) Integer size) {
        log.info("Запрос на получение данных всех бронирований пользователем с ID={}", userId);
        return bookingService.getAllBookingByUser(userId, from, size, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                                                 @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @RequestParam(value = "from", required = false) Integer from,
                                                 @RequestParam(value = "size", required = false) Integer size) {
        log.info("Запрос на получение данных всех бронирований предмета с владельцем с ID={}", ownerId);
        return bookingService.getAllBookingByOwner(ownerId, from, size, state);
    }
}
