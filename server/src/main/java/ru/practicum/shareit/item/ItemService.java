package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    OutItemDto getItem(Long itemId, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    List<OutItemDto> getItemsByOwnerId(Long ownerId);

    List<ItemDto> getSearchedItems(String searchString);
}
