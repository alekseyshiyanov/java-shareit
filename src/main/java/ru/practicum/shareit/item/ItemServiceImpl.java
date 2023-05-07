package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemInMemoryStorage itemStorage;

    @Autowired
    public ItemServiceImpl(ItemInMemoryStorage itemStorage) {
        this.itemStorage = itemStorage;
   }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        validateOwnerId(ownerId);

        Item newItem = ItemMapper.fromDto(itemDto);
        newItem.setOwner(ownerId);
        return ItemMapper.toDto(itemStorage.createItem(newItem));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        validateItemId(itemId);

        Item item = itemStorage.getItem(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        validateItemId(itemId);
        validateOwnerId(ownerId);

        Item itemForUpdate = ItemMapper.fromDto(itemDto);
        itemForUpdate.setId(itemId);
        itemForUpdate.setOwner(ownerId);
        return ItemMapper.toDto(itemStorage.updateItem(itemForUpdate));
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        validateOwnerId(ownerId);

        var itemList = itemStorage.getItemsByOwnerId(ownerId);

        return itemList.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getSearchedItems(String searchString) {
        validateSearchString(searchString);

        if (searchString.isEmpty()) {
            return new ArrayList<>();
        }

        return itemStorage.getSearchedItems(searchString).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateSearchString(String searchString) {
        if (searchString == null) {
            log.error("Строка поиска не может быть null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Строка поиска не может быть null");
        }
    }

    private void validateOwnerId(Long ownerId) {
        if (ownerId == null) {
            log.error("ID владельца не может быть null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ID владельца не может быть null");
        }
    }

    private void validateItemId(Long itemId) {
        if (itemId == null) {
            log.error("ID предмета не должен быть null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ID предмета не должен быть null");
        }

        if (itemId < 0L) {
            log.error("ID предмета должен быть положительным числом");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ID предмета должен быть положительным числом");
        }
    }
}
