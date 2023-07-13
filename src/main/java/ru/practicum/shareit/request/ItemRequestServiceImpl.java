package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ApiErrorException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.validators.PageParamValidator;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest newItemRequest = ItemRequestMapper.fromDto(itemRequestDto);

        newItemRequest.setRequester(getUserById(userId));

        ItemRequest ret = itemRequestRepository.save(newItemRequest);

        return ItemRequestMapper.toDto(ret);
    }

    public List<ItemRequestDto> getAllItemRequestByUser(Long userId) {
        checkUserById(userId);

        var itemRequestList = itemRequestRepository.getItemRequestByRequesterId(userId);

        return fillItems(itemRequestList);
    }

    public List<ItemRequestDto> getPageItemRequestByUser(Long userId, Integer from, Integer size) {
        if (from == null && size == null) {
            return getAllItemRequestByUser(userId);
        }

        if (!PageParamValidator.validate(from, size)) {
            sendErrorMessage(PageParamValidator.httpStatusCode, PageParamValidator.errorMessage);
        }

        checkUserById(userId);

        if (itemRequestRepository.existsByRequesterId(userId)) {
            return Collections.emptyList();
        }

        int start = from / size;

        var itemRequestList = itemRequestRepository.findAll(PageRequest.of(start, size)).toList();
        return fillItems(itemRequestList);
    }

    public ItemRequestDto getAllItemRequestByIdAndUser(Long requestId, Long userId) {
        if (requestId < 0) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "'requestId' не может быть отрицательным");
        }

        if (userId < 0) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "'userId' не может быть отрицательным");
        }

        checkUserById(userId);

        var ir = getItemRequestById(requestId);
        var itemList = fullTextSearchItemByNameOrDescription(ir.getDescription());

        return ItemRequestMapper.toDto(ir, itemList);
    }

    private List<ItemRequestDto> fillItems(List<ItemRequest> itemRequestList) {
        List<ItemRequestDto> result = new ArrayList<>();

        for (ItemRequest ir : itemRequestList) {
            var itemList = fullTextSearchItemByNameOrDescription(ir.getDescription());
            result.add(ItemRequestMapper.toDto(ir, itemList));
        }

        return result;
    }

    private User getUserById(Long userId) {
        return userRepository.getUserById(userId).orElseThrow(() -> {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Пользователь с ID = " + userId + " не найден в базе данных");
            return null;
        });
    }

    private ItemRequest getItemRequestById(Long itemRequestId) {
        return itemRequestRepository.getItemRequestById(itemRequestId).orElseThrow(() -> {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Запрос с ID = " + itemRequestId + " не найден в базе данных");
            return null;
        });
    }

    private void checkUserById(Long userId) {
        if (!userRepository.existsUserById(userId)) {
            sendErrorMessage(HttpStatus.NOT_FOUND, "Пользователь с ID = " + userId + " не найден в базе данных");
        }
    }

    private void sendErrorMessage(HttpStatus httpStatus, String msg) {
        log.error(msg);
        throw new ApiErrorException(httpStatus, msg);
    }

    public List<Item> fullTextSearchItemByNameOrDescription(String word){
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        QueryBuilder queryBuilder = fullTextEntityManager
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Item.class)
                .get();

        Query itemQuery = queryBuilder
                .keyword()
                .onFields("descriptionFiltered", "nameFiltered")
                .matching(word)
                .createQuery();

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(itemQuery, Item.class);

        return (List<Item>)fullTextQuery.getResultList();
    }

}
