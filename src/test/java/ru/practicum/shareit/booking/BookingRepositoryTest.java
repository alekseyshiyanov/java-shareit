package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/test_schema.sql")
class BookingRepositoryTest {

    @Autowired
    private final TestEntityManager em;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    List<User> testUserList = new ArrayList<>();
    List<Item> testItemList = new ArrayList<>();

    int testUserIndex = 0;
    int testItemIndex = 0;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(itemRepository);
        Assertions.assertNotNull(bookingRepository);
    }

    @Test
    void createBookingStandardBehavior() {
        createTestUsers(4);
        createTestItems(4);

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

    @Test
    void updateBookingStatusStandardBehavior() {
        createTestUsers(4);
        createTestItems(4);

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

    @Test
    void getAllBookingByUserInFutureStandardBehavior() {
        createTestUsers(4);
        createTestItems(4);

        Booking newBooking_1 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusHours(1))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Booking newBooking_2 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().plusMinutes(8))
                .item(testItemList.get(2))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertNotNull(bookingRepository.save(newBooking_1));
        Assertions.assertNotNull(bookingRepository.save(newBooking_2));

        var allBookings = bookingRepository.getAllBookingByBooker_IdOrderByStartDesc(testUserList.get(0).getId(), PageRequest.of(0, Integer.MAX_VALUE)).toList();
        Assertions.assertEquals(2, allBookings.size());

        var allBookingsInFuture = bookingRepository.getAllBookingByUserInFuture(testUserList.get(0).getId(), LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();
        Assertions.assertEquals(1, allBookingsInFuture.size());
        Assertions.assertEquals(testItemList.get(1).getId(), allBookingsInFuture.get(0).getItem().getId());
    }

    @Test
    void getAllByBooker_IdAndCurrentTimeStandardBehavior() {
        createTestUsers(4);
        createTestItems(4);

        Booking newBooking_1 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusHours(1))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Booking newBooking_2 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().plusMinutes(8))
                .item(testItemList.get(2))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertNotNull(bookingRepository.save(newBooking_1));
        Assertions.assertNotNull(bookingRepository.save(newBooking_2));

        var allBookings = bookingRepository.getAllBookingByBooker_IdOrderByStartDesc(testUserList.get(0).getId(), PageRequest.of(0, Integer.MAX_VALUE)).toList();
        Assertions.assertEquals(2, allBookings.size());

        var allBookingsInCurrent = bookingRepository.getAllByBooker_IdAndEndIsAfterAndStartBeforeOrderByStartDesc(testUserList.get(0).getId(),
                LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookingsInCurrent.size());
        Assertions.assertEquals(testItemList.get(2).getId(), allBookingsInCurrent.get(0).getItem().getId());
    }

    @Test
    void getAllByBookerInPastStandardBehavior() {
        createTestUsers(4);
        createTestItems(4);

        Booking newBooking_1 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusHours(1))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Booking newBooking_2 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(8))
                .item(testItemList.get(2))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertNotNull(bookingRepository.save(newBooking_1));
        Assertions.assertNotNull(bookingRepository.save(newBooking_2));

        var allBookings = bookingRepository.getAllBookingByBooker_IdOrderByStartDesc(testUserList.get(0).getId(), PageRequest.of(0, Integer.MAX_VALUE)).toList();
        Assertions.assertEquals(2, allBookings.size());

        var allBookingsInPast = bookingRepository.getAllBookingByUserInPast(testUserList.get(0).getId(),
                LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookingsInPast.size());
        Assertions.assertEquals(testItemList.get(2).getId(), allBookingsInPast.get(0).getItem().getId());
    }

    @Test
    void getAllBookingByOwnerStandardBehavior() {
        createTestUsers(4);
        createTestItems(4);

        Booking newBooking_1 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusHours(1))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Booking newBooking_2 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(8))
                .item(testItemList.get(2))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertNotNull(bookingRepository.save(newBooking_1));
        Assertions.assertNotNull(bookingRepository.save(newBooking_2));

        var allBookings = bookingRepository.getAllBookingByOwner(testUserList.get(1).getId(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookings.size());
        Assertions.assertEquals(testItemList.get(1).getId(), allBookings.get(0).getItem().getId());
    }

    @Test
    void getAllBookingByOwnerAndStatusStandardBehavior() {
        createTestUsers(4);
        createTestItems(4);

        Booking newBooking_1 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusHours(1))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.CANCELED)
                .build();

        Booking newBooking_2 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(8))
                .item(testItemList.get(2))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertNotNull(bookingRepository.save(newBooking_1));
        Assertions.assertNotNull(bookingRepository.save(newBooking_2));

        var allBookings = bookingRepository.getAllBookingByOwnerAndStatus(testUserList.get(1).getId(),
                BookingStatus.CANCELED, PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookings.size());
        Assertions.assertEquals(testItemList.get(1).getId(), allBookings.get(0).getItem().getId());
    }

    @Test
    void getAllBookingByOwnerInPastStandardBehavior() {
        createTestUsers(4);
        createTestItems(4);

        Booking newBooking_1 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusHours(1))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Booking newBooking_2 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(8))
                .item(testItemList.get(2))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertNotNull(bookingRepository.save(newBooking_1));
        Assertions.assertNotNull(bookingRepository.save(newBooking_2));

        var allBookings = bookingRepository.getAllBookingByOwnerInPast(testUserList.get(2).getId(),
                LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookings.size());
        Assertions.assertEquals(testItemList.get(2).getId(), allBookings.get(0).getItem().getId());
    }

    @Test
    void getAllBookingByOwnerInCurrentStandardBehavior() {
        createTestUsers(4);
        createTestItems(4);

        Booking newBooking_1 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now().plusHours(1))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Booking newBooking_2 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(8))
                .item(testItemList.get(2))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertNotNull(bookingRepository.save(newBooking_1));
        Assertions.assertNotNull(bookingRepository.save(newBooking_2));

        var allBookings = bookingRepository.getAllBookingByOwnerInCurrent(testUserList.get(1).getId(),
                LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookings.size());
        Assertions.assertEquals(testItemList.get(1).getId(), allBookings.get(0).getItem().getId());
    }

    @Test
    void getAllBookingByOwnerInFutureStandardBehavior() {
        createTestUsers(4);
        createTestItems(4);

        Booking newBooking_1 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(1))
                .item(testItemList.get(1))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Booking newBooking_2 = Booking.builder()
                .id(null)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(8))
                .item(testItemList.get(2))
                .booker(testUserList.get(0))
                .status(BookingStatus.WAITING)
                .build();

        Assertions.assertNotNull(bookingRepository.save(newBooking_1));
        Assertions.assertNotNull(bookingRepository.save(newBooking_2));

        var allBookings = bookingRepository.getAllBookingByOwnerInFuture(testUserList.get(1).getId(),
                LocalDateTime.now(), PageRequest.of(0, Integer.MAX_VALUE)).toList();

        Assertions.assertEquals(1, allBookings.size());
        Assertions.assertEquals(testItemList.get(1).getId(), allBookings.get(0).getItem().getId());
    }

    private void createTestUsers(int userCount) {
        Assertions.assertTrue(userCount > 0, "Количество тестовых пользователей должно быть больше 0");

        for (; testUserIndex < userCount; testUserIndex++) {
            User testUser = new User();
            testUser.setId(null);
            testUser.setEmail("test_create_" + testUserIndex + "@mail.ru");
            testUser.setName("ItemRepositoryTest Test User " + testUserIndex);

            Assertions.assertNotNull(userRepository.save(testUser));

            testUserList.add(testUser);
        }
    }

    private void createTestItems(int itemCount) {
        Assertions.assertTrue(itemCount > 0, "Количество тестовых предметов должно быть больше 0");
        Assertions.assertTrue(testUserList.size() > 0, "Количество тестовых пользователей должно быть больше 0");

        int userIndex = testItemIndex;

        if(userIndex >= testUserList.size()) {
            userIndex = testItemIndex % testUserList.size();
        }

        for (; testItemIndex < itemCount; testItemIndex++) {
            Item testItem = new Item();
            testItem.setId(null);
            testItem.setName("Test Item Name " + testItemIndex);
            testItem.setDescription("Test Item Description " + testItemIndex);
            testItem.setAvailable(false);
            testItem.setRequest(null);
            testItem.setUser(testUserList.get(userIndex++));

            if(userIndex >= testUserList.size()) {
                userIndex = 0;
            }

            Assertions.assertNotNull(itemRepository.save(testItem));

            testItemList.add(testItem);
        }
    }
}
