package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto getItem(Long itemId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    List<ItemDto> getItemsByOwnerId(Long ownerId);

    List<ItemDto> getSearchedItems(String searchString);
}
