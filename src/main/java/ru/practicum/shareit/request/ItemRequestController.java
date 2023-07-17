package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Запрос на создание новой записи");
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequest(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Запрос на получение данных всех запросов вещи пользователем с ID={}", userId);
        return itemRequestService.getAllItemRequestByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getAllItemRequestByIdAndUser(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                       @PathVariable("requestId") Long requestId) {
        log.info("Запрос на получение данных запроса вещи с requestId={} для пользователем с userId={}", requestId, userId);
        return itemRequestService.getAllItemRequestByIdAndUser(requestId, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getPageItemRequestByUser(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                         @RequestParam(value = "from", required = false) Integer from,
                                                         @RequestParam(value = "size", required = false) Integer size) {
        log.info("Запрос на постраничное получение данных всех запросов вещи пользователем с ID={} со страницы {} по {} запросов на странице",
                userId, from, size);
        return itemRequestService.getPageItemRequestByUser(userId, from, size);
    }

}
