package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.builders.ErrorMessage;
import ru.practicum.shareit.exceptions.ApiErrorException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody InputBookingDto bookingDto,
                                    @RequestHeader(value = "X-Sharer-User-Id", required = false) Long bookerId) {
        log.info("Запрос на создание новой записи");
        return bookingService.createBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvalBooking (@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
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
                                                @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Запрос на получение данных всех бронирований пользователем с ID={}", userId);
        return bookingService.getAllBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                                                 @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Запрос на получение данных всех бронирований предмета с владельцем с ID={}", ownerId);
        return bookingService.getAllBookingByOwner(ownerId, state);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errorMap = new ArrayList<>();

        for (FieldError err : e.getBindingResult().getFieldErrors()) {
            errorMap.add(err.getDefaultMessage());
        }

        return errorMap;
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleApiErrorException(ApiErrorException e) {
        var errMsg = ErrorMessage.buildRestApiErrorResponse(e.getStatusCode(), e.getMessage());
        return new ResponseEntity<>(errMsg, new HttpHeaders(), e.getStatusCode());
    }
}
