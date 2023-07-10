package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.LastNextBookingMapper;
import ru.practicum.shareit.comments.Comments;
import ru.practicum.shareit.comments.CommentsMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public static List<ItemDto> toDto(List<Item> itemList) {
        if (itemList == null) {
            return Collections.emptyList();
        }
        return itemList.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public static OutItemDto toDto(Item item, Booking lastBooking, Booking nextBooking, List<Comments> commentsList) {
        if (item == null) {
            return null;
        }

        var result = OutItemDto.builder()
                                .id(item.getId())
                                .name(item.getName())
                                .description(item.getDescription())
                                .available(item.getAvailable())
                                .request(item.getRequest())
                                .lastBooking(LastNextBookingMapper.toDto(lastBooking))
                                .nextBooking(LastNextBookingMapper.toDto(nextBooking))
                                .comments(new ArrayList<>())
                                .build();

        if (commentsList != null) {
            result.setComments(commentsList.stream()
                    .map(CommentsMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return result;
    }

    public static Item fromDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest())
                .build();
    }
}
