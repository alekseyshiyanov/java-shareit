package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Запрос на создание новой записи");
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Запрос на получение данных всех запросов вещи пользователем с ID={}", userId);
        return itemRequestClient.getAllItemRequestByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getAllItemRequestByIdAndUser(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                               @PathVariable("requestId") Long requestId) {
        log.info("Запрос на получение данных запроса вещи с requestId={} для пользователем с userId={}", requestId, userId);
        return itemRequestClient.getAllItemRequestByIdAndUser(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getPageItemRequestByUser(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                         @Valid @RequestParam(value = "from", required = false, defaultValue = 0 + "")
                                                                @PositiveOrZero(message = "Параметр 'from' должен быть положительным числом") Integer from,
                                                         @Valid @RequestParam(value = "size", required = false, defaultValue = Integer.MAX_VALUE + "")
                                                                @Positive(message = "Параметр 'from' должен быть положительным числом больше 0") Integer size) {
        log.info("Запрос на постраничное получение данных всех запросов вещи пользователем с ID={} со страницы {} по {} запросов на странице", userId, from, size);
        return itemRequestClient.getPageItemRequestByUser(userId, from, size);
    }
}
