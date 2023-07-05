package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository  itemRepository;
    private final UserRepository userRepository;
    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository  itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto createBooking(InputBookingDto bookingDto, Long bookerId) {
        Booking newBooking = BookingMapper.fromDto(bookingDto);

        newBooking.setItem(checkItem(bookingDto.getItemId()));
        newBooking.setBooker(checkUser(bookerId));

        validateBookingTime(newBooking);

        newBooking.setStatus(BookingStatus.WAITING);

        Booking ret = bookingRepository.save(newBooking);

        return BookingMapper.toDto(ret);
    }

    @Override
    public BookingDto approvalBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = getBooking(bookingId);

        if (!getOwnerId(booking).equals(ownerId)) {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Подтвердить бронирование может только владелец");
        }

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        if (booking.getStatus().equals(newStatus)) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Смена статуса бронирования не требуется");
        }

        bookingRepository.updateStatus(bookingId, newStatus);
        return BookingMapper.toDto(bookingRepository.getBookingById(bookingId));
    }

    @Override
    public BookingDto getBookingByUser(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId);

        if (!(getOwnerId(booking).equals(userId) || getBookerId(booking).equals(userId))) {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Получить данные о бронировании может только бронирующий или владелец вещи");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingByUser(Long userId, String state) {
        checkUser(userId);
        List<Booking> bookingList;

        switch(checkState(state).name()) {
            case "ALL":
                bookingList = bookingRepository.getAllBookingByBooker_IdOrderByStartDesc(userId);
                break;
            case "PAST":
                bookingList = bookingRepository.getAllBookingByUserInPast(userId, LocalDateTime.now());
                break;
            case "CURRENT":
                bookingList = bookingRepository.getAllBookingByUserInCurrent(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookingList = bookingRepository.getAllBookingByUserInFuture(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookingList = bookingRepository.getAllBookingByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingList = bookingRepository.getAllBookingByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookingList = new ArrayList<>();
        }

        return bookingList.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingByOwner(Long ownerId, String state) {
        checkUser(ownerId);
        List<Booking> bookingList;

        switch(checkState(state).name()) {
            case "ALL":
                bookingList = bookingRepository.getAllBookingByOwner(ownerId);
                break;
            case "PAST":
                bookingList = bookingRepository.getAllBookingByOwnerInPast(ownerId, LocalDateTime.now());
                break;
            case "CURRENT":
                bookingList = bookingRepository.getAllBookingByOwnerInCurrent(ownerId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookingList = bookingRepository.getAllBookingByOwnerInFuture(ownerId, LocalDateTime.now());
                break;
            case "WAITING":
                bookingList = bookingRepository.getAllBookingByOwnerAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingList = bookingRepository.getAllBookingByOwnerAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            default:
                bookingList = new ArrayList<>();
        }

        return bookingList.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
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

    private Booking getBooking(Long bookingId) {
        Booking booking = bookingRepository.getBookingById(bookingId);

        if (booking == null) {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Бронирование с ID = " + bookingId + " не найдено в базе данных");
        }

        return booking;
    }

    private Item checkItem(Long itemId) {
        Item item = itemRepository.getItemById(itemId);

        if (item == null) {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Предмет с ID = " + itemId + " не найден в базе данных");
        }

        if (!item.getAvailable()) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Предмет с ID = " + itemId + " не доступен для бронирования");
        }

        return item;
    }

    private User checkUser(Long bookerId) {
        User user = userRepository.getUserById(bookerId);

        if (user == null) {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Пользователь с ID = " + bookerId + " не найден в базе данных");
        }

        return user;
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
