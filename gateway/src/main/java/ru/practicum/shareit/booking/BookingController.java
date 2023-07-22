package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.exceptions.ApiErrorException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@Valid @RequestBody InputBookingDto bookingDto,
												@RequestHeader(value = "X-Sharer-User-Id", required = false) Long bookerId) {
		log.info("Запрос на создание новой записи");
		validateBookingTime(bookingDto);
		return bookingClient.createBooking(bookerId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approvalBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
												  @PathVariable("bookingId") Long bookingId,
												  @RequestParam("approved") Boolean approved) {
		log.info("Запрос на смену статуса бронирования с ID={} от пользователя с ID={}", bookingId, ownerId);
		return bookingClient.approvalBooking(bookingId, ownerId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingByUser(@PathVariable("bookingId") Long bookingId,
												   @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
		log.info("Запрос на получение данных бронирования с ID={} пользователем с ID={}", bookingId, userId);
		return bookingClient.getBookingByUser(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookingByUser(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
													  @RequestParam(value = "state", defaultValue = "ALL") String state,
													  @Valid @RequestParam(value = "from", required = false, defaultValue = 0 + "")
														  @PositiveOrZero(message = "Параметр 'from' должен быть положительным числом") Integer from,
													  @Valid @RequestParam(value = "size", required = false, defaultValue = Integer.MAX_VALUE + "")
														  @Positive(message = "Параметр 'from' должен быть положительным числом больше 0") Integer size) {
		log.info("Запрос на получение данных всех бронирований пользователем с ID={}", userId);
		return bookingClient.getAllBookingByUser(userId, checkState(state), from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingByOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
													   @RequestParam(value = "state", defaultValue = "ALL") String state,
													   @Valid @RequestParam(value = "from", required = false, defaultValue = 0 + "")
														   @PositiveOrZero(message = "Параметр 'from' должен быть положительным числом") Integer from,
													   @Valid @RequestParam(value = "size", required = false, defaultValue = Integer.MAX_VALUE + "")
														   @Positive(message = "Параметр 'from' должен быть положительным числом больше 0") Integer size) {
		log.info("Запрос на получение данных всех бронирований предмета с владельцем с ID={}", ownerId);
		return bookingClient.getAllBookingByOwner(ownerId, checkState(state), from, size);
	}

	private BookingState checkState(String state) {
		BookingState bookingState = BookingState.from(state).orElseThrow(() ->
				new ApiErrorException(HttpStatus.BAD_REQUEST, "Unknown state: UNSUPPORTED_STATUS"));
		return bookingState;
	}

	private void validateBookingTime(InputBookingDto booking) {
		if (booking.getStart().isAfter(booking.getEnd())) {
			throw new ApiErrorException(HttpStatus.BAD_REQUEST,
					"Время начала бронирования не может быть после времени окончания бронирования");
		}

		if (booking.getStart().isEqual(booking.getEnd())) {
			throw new ApiErrorException(HttpStatus.BAD_REQUEST,
					"Время начала бронирования не может быть равно времени окончания бронирования");
		}
	}
}
