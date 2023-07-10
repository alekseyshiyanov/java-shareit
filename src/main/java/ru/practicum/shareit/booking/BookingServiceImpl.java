package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ApiErrorException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository  itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(InputBookingDto bookingDto, Long bookerId) {
        Booking newBooking = BookingMapper.fromDto(bookingDto);

        newBooking.setItem(getItemById(bookingDto.getItemId()));
        newBooking.setBooker(getUserById(bookerId));

        validateBookingTime(newBooking);

        newBooking.setStatus(BookingStatus.WAITING);

        Booking ret = bookingRepository.save(newBooking);

        return BookingMapper.toDto(ret);
    }

    @Override
    public BookingDto approvalBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = getBookingById(bookingId);

        if (!getOwnerId(booking).equals(ownerId)) {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Подтвердить бронирование может только владелец");
        }

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        if (booking.getStatus().equals(newStatus)) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Смена статуса бронирования не требуется");
        }

        bookingRepository.updateStatus(bookingId, newStatus);
        return BookingMapper.toDto(getBookingById(bookingId));
    }

    @Override
    public BookingDto getBookingByUser(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);

        if (!(getOwnerId(booking).equals(userId) || getBookerId(booking).equals(userId))) {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Получить данные о бронировании может только бронирующий или владелец вещи");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingByUser(Long userId, String state) {
        getUserById(userId);

        switch (checkState(state)) {
            case ALL:
                return BookingMapper.toDto(bookingRepository.getAllBookingByBooker_IdOrderByStartDesc(userId));
            case PAST:
                return BookingMapper.toDto(bookingRepository.getAllBookingByUserInPast(userId, LocalDateTime.now()));
            case CURRENT:
                return BookingMapper.toDto(bookingRepository.getAllByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.toDto(bookingRepository.getAllBookingByUserInFuture(userId, LocalDateTime.now()));
            case WAITING:
                return BookingMapper.toDto(bookingRepository.getAllBookingByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.toDto(bookingRepository.getAllBookingByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED));
            default:
                return BookingMapper.toDto(new ArrayList<>());
        }
    }

    @Override
    public List<BookingDto> getAllBookingByOwner(Long ownerId, String state) {
        getUserById(ownerId);

        switch (checkState(state)) {
            case ALL:
                return BookingMapper.toDto(bookingRepository.getAllBookingByOwner(ownerId));
            case PAST:
                return BookingMapper.toDto(bookingRepository.getAllBookingByOwnerInPast(ownerId, LocalDateTime.now()));
            case CURRENT:
                return BookingMapper.toDto(bookingRepository.getAllBookingByOwnerInCurrent(ownerId, LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.toDto(bookingRepository.getAllBookingByOwnerInFuture(ownerId, LocalDateTime.now()));
            case WAITING:
                return BookingMapper.toDto(bookingRepository.getAllBookingByOwnerAndStatus(ownerId, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.toDto(bookingRepository.getAllBookingByOwnerAndStatus(ownerId, BookingStatus.REJECTED));
            default:
                return BookingMapper.toDto(new ArrayList<>());
        }
    }

    private void sendErrorMessage(HttpStatus httpStatus, String msg) {
        log.error(msg);
        throw new ApiErrorException(httpStatus, msg);
    }

    private BookingState checkState(String state) {
        BookingState bookingState = null;

        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingState;
    }

    private Long getBookerId(Booking booking) {
        return booking.getBooker().getId();
    }

    private Long getOwnerId(Booking booking) {
        return booking.getItem().getUser().getId();
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.getBookingById(bookingId).orElseThrow(() -> {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Бронирование с ID = " + bookingId + " не найдено в базе данных");
            return null;
        });
    }

    private Item getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId).orElseThrow(() -> {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Предмет с ID = " + itemId + " не найден в базе данных");
            return null;
        });

        if (!item.getAvailable()) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Предмет с ID = " + itemId + " не доступен для бронирования");
        }

        return item;
    }

    private User getUserById(Long bookerId) {
        return userRepository.getUserById(bookerId).orElseThrow(() -> {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Пользователь с ID = " + bookerId + " не найден в базе данных");
            return null;
        });
    }

    private void validateBookingTime(Booking booking) {
        if (booking.getStart().isAfter(booking.getEnd())) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Время начала бронирования не может быть после времени окончания бронирования");
        }

        if (booking.getStart().isEqual(booking.getEnd())) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Время начала бронирования не может быть равно времени окончания бронирования");
        }

        if (getOwnerId(booking).equals(getBookerId(booking))) {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Нельзя забронировать свой предмет");
        }
    }
}
