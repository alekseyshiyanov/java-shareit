package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.CommentsDto;
import ru.practicum.shareit.comments.CommentsService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentsService commentsService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        log.info("Запрос на создание новой записи");
        return itemService.createItem(itemDto, ownerId);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentsDto createComment(@RequestBody CommentsDto commentsDto,
                                     @RequestHeader(value = "X-Sharer-User-Id", required = false) Long authorId,
                                     @PathVariable("itemId") Long itemId) {
        log.info("Запрос на создание нового комментария");
        return commentsService.createComment(itemId, authorId, commentsDto);
    }


    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable("id") Long itemId,
                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        log.info("Запрос на обновление текущей записи");
        return itemService.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{id}")
    public OutItemDto getItem(@PathVariable("id") Long itemId,
                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Запрос на получение данных предмета с ID={}", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<OutItemDto> getItemsByOwnerId(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        log.info("Запрос на получение данных предметов пользователя с ID={}", ownerId);
        return itemService.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String searchString,
                                    @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        log.info("Запрос на поиск предметов со строкой поиска '{}'", searchString);
        return itemService.getSearchedItems(searchString);
    }
}
