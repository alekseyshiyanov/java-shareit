package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comments.CommentsRepository;
import ru.practicum.shareit.exceptions.ApiErrorException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentsRepository commentsRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        Item newItem = ItemMapper.fromDto(itemDto);
        validateItem(newItem);

        User ownerUser = getOwnerById(ownerId);

        newItem.setUser(ownerUser);

        return ItemMapper.toDto(itemRepository.save(newItem));
    }

    @Override
    public OutItemDto getItem(Long itemId, Long userId) {
        validateItemId(itemId);

        Item item = getItemById(itemId);

        var lastBooking = itemRepository.getLastBooking(itemId, userId, LocalDateTime.now(),
                BookingStatus.APPROVED, PageRequest.of(0,1));
        var nextBooking = itemRepository.getNextBooking(itemId, userId, LocalDateTime.now(),
                BookingStatus.APPROVED, PageRequest.of(0,1));

        var commentsList = item.getUser().getId().equals(userId) ? commentsRepository.findCommentsByItem_Id(itemId) :
                commentsRepository.findCommentsByItem_IdAndAuthor_Id(itemId, userId);

        return ItemMapper.toDto(item,
                (lastBooking == null || lastBooking.isEmpty()) ? null : lastBooking.getContent().get(0),
                (nextBooking == null || nextBooking.isEmpty()) ? null : nextBooking.getContent().get(0),
                commentsList);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        validateItemId(itemId);

        Item itemForUpdate = ItemMapper.fromDto(itemDto);
        itemForUpdate.setId(itemId);
        itemForUpdate.setUser(getOwnerById(ownerId));

        itemRepository.updateItem(itemForUpdate);

        return ItemMapper.toDto(getItemById(itemId));
    }

    @Override
    public List<OutItemDto> getItemsByOwnerId(Long ownerId) {
        validateOwnerId(ownerId);

        var itemList = itemRepository.getItemsByUser_IdOrderByIdAsc(ownerId);

        List<OutItemDto> itemDtoList = new ArrayList<>();

        for (Item item : itemList) {
            var lastBooking = itemRepository.getLastBooking(item.getId(), ownerId, LocalDateTime.now(),
                    BookingStatus.APPROVED, PageRequest.of(0,1));
            var nextBooking = itemRepository.getNextBooking(item.getId(), ownerId, LocalDateTime.now(),
                    BookingStatus.APPROVED, PageRequest.of(0,1));
            var commentsList = commentsRepository.findCommentsByItem_IdAndAuthor_Id(item.getId(), ownerId);

            itemDtoList.add(ItemMapper.toDto(item,
                    (lastBooking == null || lastBooking.isEmpty()) ? null : lastBooking.getContent().get(0),
                    (nextBooking == null || nextBooking.isEmpty()) ? null : nextBooking.getContent().get(0),
                    commentsList)
            );
        }
        return itemDtoList;
    }

    @Override
    public List<ItemDto> getSearchedItems(String searchString) {
        validateSearchString(searchString);

        if (searchString.isEmpty()) {
            return Collections.emptyList();
        }

        return ItemMapper.toDto(itemRepository.getItemsByDescriptionContainsIgnoreCaseAndAvailableIsTrue(searchString));
    }

    private void validateSearchString(String searchString) {
        if (searchString == null) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Строка поиска не может быть null");
        }
    }

    private void validateOwnerId(Long ownerId) {
        if (ownerId == null) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "ID владельца не может быть null");
        }
    }

    private void validateItemId(Long itemId) {
        if (itemId == null) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "ID предмета не должен быть null");
        }

        if (itemId < 0L) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "ID предмета должен быть положительным числом");
        }
    }

    private void validateItem(Item item) {
        if (item.getAvailable() == null) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Доступность предмета не может быть null");
        }

        if ((item.getDescription() == null) || (item.getDescription().isBlank())) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Описание предмета предмета не может быть пустым или null");
        }

        if ((item.getName() == null) || (item.getName().isBlank())) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Название предмета предмета не может быть пустым или null");
        }
    }

    private User getOwnerById(Long ownerId) {
        validateOwnerId(ownerId);

        return userRepository.getUserById(ownerId).orElseThrow(() -> {
            sendErrorMessage(HttpStatus.NOT_FOUND,"Пользователь с ID = " + ownerId + " не найден в базе данных");
            return null;
        });
    }

    private Item getItemById(Long itemId) {
        return itemRepository.getItemById(itemId).orElseThrow(() -> {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Предмет с ID = " + itemId + " не найден в базе данных");
            return null;
        });
    }

    private void sendErrorMessage(HttpStatus httpStatus, String msg) {
        log.error(msg);
        throw new ApiErrorException(httpStatus, msg);
    }
}
