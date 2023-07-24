package ru.practicum.shareit.request;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest, List<Item> itemList) {
        if (itemRequest == null) {
            return null;
        }
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(UserMapper.toDto(itemRequest.getRequester()))
                .items(ItemMapper.toDto(itemList))
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(UserMapper.toDto(itemRequest.getRequester()))
                .items(ItemMapper.toDto(Collections.emptyList()))
                .created(itemRequest.getCreated())
                .build();
    }

    public static List<ItemRequestDto> toDto(List<ItemRequest> itemRequestList) {
        if (itemRequestList == null) {
            return Collections.emptyList();
        }
        return itemRequestList.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public static ItemRequest fromDto(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(null)
                .description(itemRequestDto.getDescription())
                .requester(null)
                .created(LocalDateTime.now())
                .build();
    }
}
