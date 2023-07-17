package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exceptions.ApiErrorException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql({
        "/test_schema.sql",
        "/import_user_data.sql",
        "/import_item_request_data.sql",
        "/import_item_data.sql",
        "/import_booking_data.sql",
        "/import_comments_data.sql"
})
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplUnitTest {

    private final BookingService service;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(service);
    }

    @Test
    @Order(2)
    void approvalBookingStandardBehavior() {
        var bookingForChange = service.getBookingByUser(1000L, 1000L);
        Assertions.assertEquals(BookingStatus.APPROVED, bookingForChange.getStatus());

        var changedBooking = service.approvalBooking(4000L, 1000L, false);
        Assertions.assertEquals(BookingStatus.REJECTED, changedBooking.getStatus());
    }

    @Test
    @Order(3)
    void approvalBookingSameStatusBehavior() {
        var bookingForChange = service.getBookingByUser(1000L, 1000L);
        Assertions.assertEquals(BookingStatus.APPROVED, bookingForChange.getStatus());

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.approvalBooking(4000L, 1000L, true));

        Assertions.assertTrue(ex.getMessage().contains("Смена статуса бронирования не требуется"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(4)
    void approvalBookingNotOwnerBehavior() {
        var bookingForChange = service.getBookingByUser(1000L, 1000L);
        Assertions.assertEquals(BookingStatus.APPROVED, bookingForChange.getStatus());

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.approvalBooking(5000L, 1000L, true));

        Assertions.assertTrue(ex.getMessage().contains("Подтвердить бронирование может только владелец"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @Order(5)
    void getAllBookingByUserStateAllBehavior() {
        var bookingList = service.getAllBookingByUser(1000L, 0, 20, "ALL");
        Assertions.assertEquals(5, bookingList.size());
    }

    @Test
    @Order(6)
    void getAllBookingByUserStatePastBehavior() {
        var bookingList = service.getAllBookingByUser(1000L, 0, 20, "PAST");
        Assertions.assertEquals(3, bookingList.size());
    }

    @Test
    @Order(7)
    void getAllBookingByUserStateFutureBehavior() {
        var bookingList = service.getAllBookingByUser(1000L, 0, 20, "FUTURE");
        Assertions.assertEquals(1, bookingList.size());
    }

    @Test
    @Order(8)
    void getAllBookingByUserStateCurrentBehavior() {
        var bookingList = service.getAllBookingByUser(1000L, 0, 20, "CURRENT");
        Assertions.assertEquals(1, bookingList.size());
    }

    @Test
    @Order(9)
    void getAllBookingByUserStateWaitingBehavior() {
        var bookingList = service.getAllBookingByUser(1000L, 0, 20, "WAITING");
        Assertions.assertEquals(0, bookingList.size());
    }

    @Test
    @Order(10)
    void getAllBookingByUserStateRejectedBehavior() {
        var bookingList = service.getAllBookingByUser(1000L, 0, 20, "REJECTED");
        Assertions.assertEquals(1, bookingList.size());
    }

    @Test
    @Order(11)
    void getAllBookingByUserStateBadBehavior() {
        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.getAllBookingByUser(1000L, 0, 20, "Bad"));

        Assertions.assertTrue(ex.getMessage().contains("Unknown state: UNSUPPORTED_STATUS"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(12)
    void getAllBookingByUserStateRejectedMaxRecordsBehavior() {
        var bookingList = service.getAllBookingByUser(1000L, null, null, "REJECTED");
        Assertions.assertEquals(1, bookingList.size());
    }

    @Test
    @Order(13)
    void getAllBookingByUserStateRejectedBadPageParamBehavior() {
        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.getAllBookingByUser(1000L, null, 20, "REJECTED"));

        Assertions.assertTrue(ex.getMessage().contains("Параметр запроса 'from' не может быть null"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }



    @Test
    @Order(14)
    void getAllBookingByOwnerStateAllBehavior() {
        var bookingList = service.getAllBookingByOwner(4000L, 0, 20, "ALL");
        Assertions.assertEquals(5, bookingList.size());
    }

    @Test
    @Order(15)
    void getAllBookingByOwnerStatePastBehavior() {
        var bookingList = service.getAllBookingByOwner(4000L, 0, 20, "PAST");
        Assertions.assertEquals(3, bookingList.size());
    }

    @Test
    @Order(16)
    void getAllBookingByOwnerStateFutureBehavior() {
        var bookingList = service.getAllBookingByOwner(4000L, 0, 20, "FUTURE");
        Assertions.assertEquals(1, bookingList.size());
    }

    @Test
    @Order(17)
    void getAllBookingByOwnerStateCurrentBehavior() {
        var bookingList = service.getAllBookingByOwner(4000L, 0, 20, "CURRENT");
        Assertions.assertEquals(1, bookingList.size());
    }

    @Test
    @Order(18)
    void getAllBookingByOwnerStateWaitingBehavior() {
        var bookingList = service.getAllBookingByOwner(4000L, 0, 20, "WAITING");
        Assertions.assertEquals(0, bookingList.size());
    }

    @Test
    @Order(19)
    void getAllBookingByOwnerStateRejectedBehavior() {
        var bookingList = service.getAllBookingByOwner(4000L, 0, 20, "REJECTED");
        Assertions.assertEquals(1, bookingList.size());
    }

    @Test
    @Order(20)
    void getAllBookingByOwnerStateBadBehavior() {
        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.getAllBookingByOwner(4000L, 0, 20, "Bad"));

        Assertions.assertTrue(ex.getMessage().contains("Unknown state: UNSUPPORTED_STATUS"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(21)
    void getAllBookingByOwnerStateRejectedMaxRecordsBehavior() {
        var bookingList = service.getAllBookingByOwner(4000L, null, null, "REJECTED");
        Assertions.assertEquals(1, bookingList.size());
    }

    @Test
    @Order(22)
    void getAllBookingByOwnerStateRejectedBadPageParamBehavior() {
        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.getAllBookingByOwner(4000L, null, 20, "REJECTED"));

        Assertions.assertTrue(ex.getMessage().contains("Параметр запроса 'from' не может быть null"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(23)
    void getBookingByBookerStandardBehavior() {
        var booking = service.getBookingByUser(4000L, 5000L);

        Assertions.assertEquals(4000L, booking.getId());
        Assertions.assertEquals(2000L, booking.getItem().getId());
        Assertions.assertEquals("Отвертка", booking.getItem().getName());
        Assertions.assertEquals("Аккумуляторная отвертка", booking.getItem().getDescription());
    }

    @Test
    @Order(24)
    void getBookingByOwnerStandardBehavior() {
        var booking = service.getBookingByUser(4000L, 4000L);

        Assertions.assertEquals(4000L, booking.getId());
        Assertions.assertEquals(2000L, booking.getItem().getId());
        Assertions.assertEquals("Отвертка", booking.getItem().getName());
        Assertions.assertEquals("Аккумуляторная отвертка", booking.getItem().getDescription());
    }

    @Test
    @Order(25)
    void getBookingByUserWrongUserBehavior() {
        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.getBookingByUser(4000L, 1000L));

        Assertions.assertTrue(ex.getMessage().contains("Получить данные о бронировании может только бронирующий или владелец вещи"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @Order(26)
    void createBookingStandardBehavior() {
        InputBookingDto newBookingDto = InputBookingDto.builder()
                .itemId(5000L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(10))
                .build();

        var booking = service.createBooking(newBookingDto, 6000L);

        Assertions.assertNotNull(booking.getId());
        Assertions.assertEquals(5000L, booking.getItem().getId());
        Assertions.assertEquals(BookingStatus.WAITING, booking.getStatus());
        Assertions.assertEquals("Стандартная щётка для обуви", booking.getItem().getDescription());
    }

    @Test
    @Order(27)
    void createBookingNoItemBehavior() {
        InputBookingDto newBookingDto = InputBookingDto.builder()
                .itemId(7000L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(10))
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.createBooking(newBookingDto, 6000L));

        Assertions.assertTrue(ex.getMessage().contains("не найден в базе данных"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @Order(28)
    void createBookingItemNoAvailableBehavior() {
        InputBookingDto newBookingDto = InputBookingDto.builder()
                .itemId(6000L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(10))
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.createBooking(newBookingDto, 6000L));

        Assertions.assertTrue(ex.getMessage().contains("не доступен для бронирования"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(29)
    void createBookingNoBookerBehavior() {
        InputBookingDto newBookingDto = InputBookingDto.builder()
                .itemId(5000L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(10))
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.createBooking(newBookingDto, 60000L));

        Assertions.assertTrue(ex.getMessage().contains("Пользователь с ID = "));
        Assertions.assertTrue(ex.getMessage().contains("не найден в базе данных"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @Order(29)
    void createBookingByOwnerBehavior() {
        InputBookingDto newBookingDto = InputBookingDto.builder()
                .itemId(5000L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(10))
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.createBooking(newBookingDto, 4000L));

        Assertions.assertTrue(ex.getMessage().contains("Нельзя забронировать свой предмет"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @Order(30)
    void createBookingWrongEndTimeBehavior() {
        InputBookingDto newBookingDto = InputBookingDto.builder()
                .itemId(5000L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusMinutes(10))
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.createBooking(newBookingDto, 6000L));

        Assertions.assertTrue(ex.getMessage().contains("Время начала бронирования не может быть после времени окончания бронирования"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(31)
    void createBookingWrongStartTimeBehavior() {
        var bookingTime = LocalDateTime.now().plusHours(1);

        InputBookingDto newBookingDto = InputBookingDto.builder()
                .itemId(5000L)
                .start(bookingTime)
                .end(bookingTime)
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.createBooking(newBookingDto, 6000L));

        Assertions.assertTrue(ex.getMessage().contains("Время начала бронирования не может быть равно времени окончания бронирования"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(32)
    void getBookingByBookerNoBookingBehavior() {
        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.getBookingByUser(40000L, 5000L));

        Assertions.assertTrue(ex.getMessage().contains("Бронирование с ID = "));
        Assertions.assertTrue(ex.getMessage().contains("не найдено в базе данных"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
