package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Rollback
@Sql({
        "/test_schema.sql",
        "/import_user_data.sql",
        "/import_item_request_data.sql",
        "/import_item_data.sql",
        "/import_booking_data.sql",
        "/import_comments_data.sql"
})
class BookingRepositoryTest {

    @Autowired
    private final TestEntityManager em;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    List<User> testUserList;
    List<Item> testItemList;

    @Order(1)
    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(itemRepository);
        Assertions.assertNotNull(bookingRepository);
    }

    @Order(2)
    @Test
    void checkTestData() {
        fillTestUsers();
        fillTestItems();

        Assertions.assertEquals(4, testUserList.size());
        Assertions.assertEquals(6, testItemList.size());
    }

    @Order(3)
    @Test
    void createBookingStandardBehavior() {
        fillTestUsers();
        fillTestItems();

        Booking newBooking = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusHours(1L))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertNull(newBooking.getId());
        bookingRepository.save(newBooking);
        Assertions.assertNotNull(newBooking.getId());
    }

    @Order(4)
    @Test
    void updateBookingStatusStandardBehavior() {
        fillTestUsers();
        fillTestItems();

        Booking newBooking = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusHours(1L))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertNull(newBooking.getId());
        bookingRepository.save(newBooking);

        Assertions.assertNotNull(newBooking.getId());
        Assertions.assertEquals(BookingStatus.WAITING, newBooking.getStatus());

        bookingRepository.updateStatus(newBooking.getId(), BookingStatus.APPROVED);

        var updatedBookingOpt = bookingRepository.getBookingById(newBooking.getId());
        Assertions.assertTrue(updatedBookingOpt.isPresent());

        var updatedBooking = updatedBookingOpt.get();
        Assertions.assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
    }

    @Order(5)
    @Test
    void getAllBookingByUserInFutureStandardBehavior() {
        var allBookings = bookingRepository.getAllBookingByBooker_IdOrderByStartDesc(1000L, PageRequest.of(0, Integer.MAX_VALUE)).toList();
        Assertions.assertEquals(5, allBookings.size());

        var allBookingsInFuture = bookingRepository.getAllBookingByUserInFuture(1000L, LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();
        Assertions.assertEquals(1, allBookingsInFuture.size());
    }

    @Order(6)
    @Test
    void getAllByBooker_IdAndCurrentTimeStandardBehavior() {
        var allBookings = bookingRepository.getAllBookingByBooker_IdOrderByStartDesc(1000L, PageRequest.of(0, Integer.MAX_VALUE)).toList();
        Assertions.assertEquals(5, allBookings.size());

        var allBookingsInCurrent = bookingRepository.getAllByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc(1000L,
                LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookingsInCurrent.size());
    }

    @Order(7)
    @Test
    void getAllByBookerInPastStandardBehavior() {
        var allBookings = bookingRepository.getAllBookingByBooker_IdOrderByStartDesc(1000L, PageRequest.of(0, Integer.MAX_VALUE)).toList();
        Assertions.assertEquals(5, allBookings.size());

        var allBookingsInPast = bookingRepository.getAllBookingByUserInPast(1000L,
                LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(3, allBookingsInPast.size());
    }

    @Order(8)
    @Test
    void getAllBookingByOwnerStandardBehavior() {
        var allBookings0 = bookingRepository.getAllBookingByOwner(4000L, PageRequest.of(0, Integer.MAX_VALUE)).toList();
        Assertions.assertEquals(5, allBookings0.size());

        var allBookings1 = bookingRepository.getAllBookingByOwner(6000L, PageRequest.of(0, Integer.MAX_VALUE)).toList();
        Assertions.assertEquals(1, allBookings1.size());
    }

    @Order(9)
    @Test
    void getAllBookingByOwnerAndStatusStandardBehavior() {
        fillTestUsers();
        fillTestItems();

        Booking newBooking1 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusHours(1))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.CANCELED)
                .build();

        Assertions.assertNotNull(bookingRepository.save(newBooking1));

        var allBookings = bookingRepository.getAllBookingByOwnerAndStatus(testUserList.get(1).getId(),
                BookingStatus.CANCELED, PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookings.size());
        Assertions.assertEquals(testItemList.get(1).getId(), allBookings.get(0).getItem().getId());
    }

    @Order(10)
    @Test
    void getAllBookingByOwnerInPastStandardBehavior() {
        var allBookings = bookingRepository.getAllBookingByOwnerInPast(4000L,
                LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(3, allBookings.size());
        Assertions.assertEquals(2000L, allBookings.get(0).getItem().getId());
    }

    @Order(11)
    @Test
    void getAllBookingByOwnerInCurrentStandardBehavior() {
        var allBookings = bookingRepository.getAllBookingByOwnerInCurrent(4000L,
                LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookings.size());
        Assertions.assertEquals(3000L, allBookings.get(0).getItem().getId());
    }

    @Order(12)
    @Test
    void getAllBookingByOwnerInFutureStandardBehavior() {
        var allBookings = bookingRepository.getAllBookingByOwnerInFuture(4000L,
                LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookings.size());
        Assertions.assertEquals(2000L, allBookings.get(0).getItem().getId());
    }

    private void fillTestUsers() {
        testUserList = userRepository.findAll();
        Assertions.assertTrue(testUserList.size() > 0, "Количество тестовых пользователей должно быть больше 0");
    }

    private void fillTestItems() {
        testItemList = itemRepository.findAll();
        Assertions.assertTrue(testItemList.size() > 0, "Количество тестовых предметов должно быть больше 0");
    }
}
