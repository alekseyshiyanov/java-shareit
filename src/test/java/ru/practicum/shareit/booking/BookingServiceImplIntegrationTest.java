package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exceptions.ApiErrorException;

import javax.transaction.Transactional;

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
class BookingServiceImplIntegrationTest {

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

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            service.approvalBooking(4000L, 1000L, true);
        });

        Assertions.assertTrue(ex.getMessage().contains("Смена статуса бронирования не требуется"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(4)
    void approvalBookingNotOwnerBehavior() {
        var bookingForChange = service.getBookingByUser(1000L, 1000L);
        Assertions.assertEquals(BookingStatus.APPROVED, bookingForChange.getStatus());

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            service.approvalBooking(5000L, 1000L, true);
        });

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
        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            service.getAllBookingByUser(1000L, 0, 20, "Bad");
        });

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
        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            service.getAllBookingByUser(1000L, null, 20, "REJECTED");
        });

        Assertions.assertTrue(ex.getMessage().contains("Параметр запроса 'from' не может быть null"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}