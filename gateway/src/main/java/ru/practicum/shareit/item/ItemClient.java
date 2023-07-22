package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comments.CommentsDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDto itemDto, Long ownerId) {
        return post("/", ownerId, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> getItem(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByOwnerId(Long ownerId) {
        return get("/", ownerId);
    }

    public ResponseEntity<Object> getSearchedItems(Long ownerId, String searchString) {
        Map<String, Object> parameters = Map.of(
             "text", searchString
        );
        return get("/search?text={text}", ownerId, parameters);
    }

    public ResponseEntity<Object> createComment(CommentsDto commentsDto, Long itemId, Long authorId) {
        return post("/" + itemId + "/comment", authorId, commentsDto);
    }
}
