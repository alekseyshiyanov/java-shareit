package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking getBookingById(Long id);

    Booking getTopBookingByItem_IdAndBooker_IdAndEndBeforeOrderByEndDesc(Long itemId, Long bookerId, LocalDateTime current);

    @Modifying(clearAutomatically = true)
    @Query("update Booking b " +
            "set b.status = :status " +
            "where b.id = :bookingId")
    void updateStatus(@Param("bookingId") Long bookingId, @Param("status") BookingStatus status);

    List<Booking> getAllBookingByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> getAllBookingByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    @Modifying(clearAutomatically = true)
    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.start > :current " +
            "order by b.start desc")
    List<Booking> getAllBookingByUserInFuture(@Param("bookerId") Long bookerId, @Param("current") LocalDateTime current);

    @Modifying(clearAutomatically = true)
    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "and :current between b.start and b.end " +
            "order by b.start asc")
    List<Booking> getAllBookingByUserInCurrent(@Param("bookerId") Long bookerId, @Param("current") LocalDateTime current);

    @Modifying(clearAutomatically = true)
    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.end < :current " +
            "order by b.start desc")
    List<Booking> getAllBookingByUserInPast(@Param("bookerId") Long bookerId, @Param("current") LocalDateTime current);

    @Modifying(clearAutomatically = true)
    @Query("select b " +
            "from Booking b " +
            "where b.item.user.id = :ownerId " +
            "order by b.start desc")
    List<Booking> getAllBookingByOwner(@Param("ownerId") Long ownerId);

    @Modifying(clearAutomatically = true)
    @Query("select b " +
            "from Booking b " +
            "where b.item.user.id = :ownerId " +
            "and b.start > :current " +
            "order by b.start desc")
    List<Booking> getAllBookingByOwnerInFuture(@Param("ownerId") Long ownerId, @Param("current") LocalDateTime current);

    @Modifying(clearAutomatically = true)
    @Query("select b " +
            "from Booking b " +
            "where b.item.user.id = :ownerId " +
            "and :current between b.start and b.end " +
            "order by b.start desc")
    List<Booking> getAllBookingByOwnerInCurrent(@Param("ownerId") Long ownerId, @Param("current") LocalDateTime current);

    @Modifying(clearAutomatically = true)
    @Query("select b " +
            "from Booking b " +
            "where b.item.user.id = :ownerId " +
            "and b.end < :current " +
            "order by b.start desc")
    List<Booking> getAllBookingByOwnerInPast(@Param("ownerId") Long ownerId, @Param("current") LocalDateTime current);

    @Modifying(clearAutomatically = true)
    @Query("select b " +
            "from Booking b " +
            "where b.item.user.id = :ownerId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> getAllBookingByOwnerAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status);
}
