package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllItemRequestByUser(Long userId);

    List<ItemRequestDto> getPageItemRequestByUser(Long userId, Integer from, Integer size);

    ItemRequestDto getAllItemRequestByIdAndUser(Long requestId, Long userId);
}
