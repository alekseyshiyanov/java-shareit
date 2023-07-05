package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Transactional
    Item getItemById(Long id);

    @Transactional
    List<Item> getItemsByUser_IdOrderByIdAsc(@Param("ownerId") Long ownerId);

    @Transactional
    List<Item> getItemsByDescriptionContainsIgnoreCaseAndAvailableIsTrue(@Param("searchString") String searchString);

    @Modifying
    @Transactional
    @Query("update Item i " +
            "set i.name    = COALESCE(CAST(:#{#newItem.name}  as string), i.name), " +
            "i.description = COALESCE(CAST(:#{#newItem.description}  as string), i.description), " +
            "i.available   = COALESCE(CAST(CAST(:#{#newItem.available} as string) as boolean), i.available), " +
            "i.user        = COALESCE(:#{#newItem.user}, i.user), " +
            "i.request     = :#{#newItem.request} " +
            "where i.id    = :#{#newItem.id}")
    void updateItem(@Param("newItem") Item newItem);

    @Transactional
    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId " +
            "and b.item.user.id = :userId " +
            "and b.start < :current " +
            "and b.status = :status " +
            "order by b.start desc "
    )
    Page<Booking> getLastBooking(@Param("itemId") Long itemId,
                                 @Param("userId") Long userId,
                                 @Param("current")LocalDateTime current,
                                 @Param("status")BookingStatus status,
                                 Pageable pageable);

    @Transactional
    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId " +
            "and b.item.user.id = :userId " +
            "and b.start > :current " +
            "and b.status = :status " +
            "order by b.start asc")
    Page<Booking> getNextBooking(@Param("itemId") Long itemId,
                                 @Param("userId") Long userId,
                                 @Param("current")LocalDateTime current,
                                 @Param("status")BookingStatus status,
                                 Pageable pageable);
}