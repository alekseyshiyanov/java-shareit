package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.CommentsDto;
import ru.practicum.shareit.labels.Create;
import ru.practicum.shareit.labels.Update;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Validated(Create.class) ItemDto itemDto,
                                             @Valid @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                                    @NotNull(message = "Значение 'ownerId' не может быть равно null")
                                                    @PositiveOrZero(message = "Значение 'ownerId' не может быть отрицательным числом") Long ownerId) {
        log.info("Запрос на создание новой записи");
        return itemClient.createItem(itemDto, ownerId);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentsDto commentsDto,
                                                @Valid @PathVariable("itemId")
                                                       @NotNull(message = "Значение 'itemId' не может быть равно null")
                                                       @Positive(message = "Значение 'itemId' должно быть положительным числом больше нуля") Long itemId,
                                                @Valid @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                                       @NotNull(message = "Значение 'authorId' не может быть равно null")
                                                       @PositiveOrZero(message = "Значение 'authorId' не может быть отрицательным числом") Long authorId) {
        log.info("Запрос на создание нового комментария");
        return itemClient.createComment(commentsDto, itemId, authorId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody @Validated(Update.class) ItemDto itemDto,
                                             @Valid @PathVariable("id")
                                                    @NotNull(message = "Значение 'itemId' не может быть равно null")
                                                    @Positive(message = "Значение 'itemId' должно быть положительным числом больше нуля") Long itemId,
                                             @Valid @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                                    @NotNull(message = "Значение 'ownerId' не может быть равно null")
                                                    @PositiveOrZero(message = "Значение 'ownerId' не может быть отрицательным числом") Long ownerId) {
        log.info("Запрос на обновление текущей записи");
        return itemClient.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@Valid @PathVariable("id")
                                                 @NotNull(message = "Значение 'itemId' не может быть равно null")
                                                 @Positive(message = "Значение 'itemId' должно быть положительным числом больше нуля") Long itemId,
                                          @Valid @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                                 @NotNull(message = "Значение 'userId' не может быть равно null")
                                                 @PositiveOrZero(message = "Значение 'userId' не может быть отрицательным числом") Long userId) {
        log.info("Запрос на получение данных предмета с ID={}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(@Valid @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                                           @NotNull(message = "Значение 'ownerId' не может быть равно null")
                                                           @PositiveOrZero(message = "Значение 'ownerId' не может быть отрицательным числом") Long ownerId) {
        log.info("Запрос на получение данных предметов пользователя с ID={}", ownerId);
        return itemClient.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@Valid @RequestParam(name = "text")
                                                    @NotNull(message = "Строка поиска не может быть равна null") String searchString,
                                             @Valid @RequestHeader(value = "X-Sharer-User-Id", required = false)
                                                    @NotNull(message = "Значение 'ownerId' не может быть равно null")
                                                    @PositiveOrZero(message = "Значение 'ownerId' не может быть отрицательным числом") Long ownerId) {
        log.info("Запрос на поиск предметов со строкой поиска '{}'", searchString);
        return itemClient.getSearchedItems(ownerId, searchString);
    }
}
