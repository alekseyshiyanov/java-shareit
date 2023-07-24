package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;

    private String description;

    private UserDto requester;

    private List<ItemDto> items;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created;
}