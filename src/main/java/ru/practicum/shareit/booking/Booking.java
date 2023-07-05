package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking", schema = "public")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Booking_Id")
    private Long id;

    @Column(name = "Start_Time", nullable = false)
    private LocalDateTime start;

    @Column(name = "End_Time", nullable = false)
    private LocalDateTime end;

    @OneToOne
    @JoinColumn(name = "Item_Id")
    private Item item;

    @OneToOne
    @JoinColumn(name = "Booker_Id")
    private User booker;

    @Enumerated
    @Column(name = "Status", nullable = false)
    private BookingStatus status;
}

