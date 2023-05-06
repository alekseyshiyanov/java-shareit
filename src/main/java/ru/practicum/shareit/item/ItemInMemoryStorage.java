package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.UserInMemoryStorage;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemInMemoryStorage {

    private Integer itemUID = 0;
    private final HashMap<Integer, Item> items = new HashMap<>();
    private final UserInMemoryStorage userStorage;

    @Autowired
    public ItemInMemoryStorage(UserInMemoryStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Item createItem(Item item) {
        addItem(item);
        return item;
    }

    public Item getItem(Integer itemId) {
        return checkItem(itemId);
    }

    public Item updateItem(Item item) {
        Integer uid = item.getId();
        Item oldItem = getItem(uid);

        if (!oldItem.getOwner().equals(item.getOwner())) {
            log.error("Нельзя сменить владельца предмета");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Нельзя сменить владельца предмета");
        }

        log.info("Обновляем объект с ID: {}", uid);
        log.info("Объект до обновления: {}", oldItem);

        Item newItem = Item.builder()
                .id(uid)
                .name((item.getName() == null) ? oldItem.getName() : item.getName())
                .description((item.getDescription() == null) ? oldItem.getDescription() : item.getDescription())
                .available((item.getAvailable() == null) ? oldItem.getAvailable() : item.getAvailable())
                .owner(oldItem.getOwner())
                .request((item.getRequest() == null) ? oldItem.getRequest() : item.getRequest())
                .build();

        validateItem(newItem);

        items.put(uid, newItem);

        Item currentItem = getItem(uid);

        log.info("Объект после обновления: {}", currentItem);

        return currentItem;
    }

    public List<Item> getItemsByOwnerId(Integer ownerId) {
        validateOwnerId(ownerId);

        return items.values().stream()
                .filter(i -> i.getOwner().equals(ownerId))
                .collect(Collectors.toList());
    }

    private void addItem(Item item) {
        validateItem(item);

        Integer uid = getItemUID();
        item.setId(uid);
        items.put(uid, item);

        log.info("Сохранен объект: {}", item);
    }

    public List<Item> getSearchedItems(String searchString) {
        return items.values().stream()
                .filter(i -> (i.getDescription().toLowerCase().contains(searchString.toLowerCase()) && (i.getAvailable().equals(true))))
                .collect(Collectors.toList());
    }

    private Item checkItem(Integer uid) {
        Item item = items.get(uid);
        if (item == null) {
            log.error("Предмет с ID = {} не существует", uid);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Предмет с ID = " + uid + " не существует");
        }
        return item;
    }

    private void validateItem(Item item) {
        if (item.getName() == null) {
            log.error("Название предмета не может быть null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Название предмета не может быть null");
        }
        if (item.getName().isBlank()) {
            log.error("Название предмета не может быть пустым или состоять только из пробелов");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Название предмета не может быть пустым или состоять только из пробелов");
        }
        if (item.getDescription() == null) {
            log.error("Описание предмета не может быть null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Описание предмета не может быть null");
        }
        if (item.getDescription().isBlank()) {
            log.error("Описание предмета не может быть пустым или состоять только из пробелов");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Описание предмета не может быть пустым или состоять только из пробелов");
        }
        if (item.getAvailable() == null) {
            log.error("Статус доступности пердмета не может быть null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Статус доступности пердмета не может быть null");
        }
        validateOwnerId(item.getOwner());
    }

    private void validateOwnerId(Integer ownerId) {
        if (!userStorage.userIsPresent(ownerId)) {
            log.error("ID владельца не существует");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "ID владельца не существует");
        }
    }

    private Integer getItemUID() {
        return ++itemUID;
    }
}
