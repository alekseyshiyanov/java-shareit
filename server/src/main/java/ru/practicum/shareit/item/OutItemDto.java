package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.LastNextBookingDto;
import ru.practicum.shareit.comments.CommentsDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Integer request;

    private LastNextBookingDto lastBooking;

    private LastNextBookingDto nextBooking;

    List<CommentsDto> comments;
}
