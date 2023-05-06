package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Integer ownerId);

    ItemDto getItem(Integer itemId);

    ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer ownerId);

    List<ItemDto> getItemsByOwnerId(Integer ownerId);

    List<ItemDto> getSearchedItems(String searchString);
}
