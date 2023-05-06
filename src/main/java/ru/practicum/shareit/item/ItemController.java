package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId) {
        log.info("Запрос на создание новой записи");
        return itemService.createItem(itemDto, ownerId);
    }


    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable("id") Integer itemId,
                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId) {
        log.info("Запрос на обновление текущей записи");
        return itemService.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable("id") Integer itemId,
                           @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId) {
        log.info("Запрос на получение данных предмета с ID={}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(@RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId) {
        log.info("Запрос на получение данных предметов пользователя с ID={}", ownerId);
        return itemService.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String searchString,
                                    @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer ownerId) {
        log.info("Запрос на поиск предметов со строкой поиска '{}'", searchString);
        return itemService.getSearchedItems(searchString);
    }

}
