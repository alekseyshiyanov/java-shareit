package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exceptions.ApiErrorException;

import javax.transaction.Transactional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql({
        "/test_schema.sql",
        "/import_user_data.sql",
        "/import_item_request_data.sql",
        "/import_item_data.sql",
        "/import_booking_data.sql",
        "/import_comments_data.sql"
})
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplUnitTest {

    private final ItemService service;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(service);
    }

    @Test
    @Order(2)
    void getItemStandardBehavior() {
        var item = service.getItem(1000L, 1000L);
        Assertions.assertEquals(1000L, item.getId());
        Assertions.assertEquals("Аккумуляторная дрель", item.getName());
        Assertions.assertTrue(item.getAvailable());
        Assertions.assertNull(item.getLastBooking());
        Assertions.assertEquals(7000L, item.getNextBooking().getId());
    }

    @Test
    @Order(3)
    void getItemsByOwnerIdStandardBehavior() {
        var itemList = service.getItemsByOwnerId(4000L);

        Assertions.assertEquals(4, itemList.size());

        Assertions.assertEquals(2000L, itemList.get(0).getId());
        Assertions.assertEquals(3000L, itemList.get(1).getId());
        Assertions.assertEquals(5000L, itemList.get(2).getId());
        Assertions.assertEquals(6000L, itemList.get(3).getId());
    }

    @Test
    @Order(4)
    void getSearchedItemsStandardBehavior() {
        String searchString = "сТоЛ";
        var itemList = service.getSearchedItems(searchString);

        Assertions.assertEquals(1, itemList.size());
        Assertions.assertTrue(itemList.get(0).getDescription().toLowerCase().contains(searchString.toLowerCase()));
    }

    @Test
    @Order(5)
    void getSearchedItemsEmptyStringBehavior() {
        String searchString = "";
        var itemList = service.getSearchedItems(searchString);

        Assertions.assertEquals(0, itemList.size());
    }

    @Test
    @Order(6)
    void updateItemStandardBehavior() {
        var currentItem = service.getItem(2000L, 4000L);

        ItemDto itemForUpdateDto = ItemDto.builder()
                .id(null)
                .name("Отвертка update")
                .description("Аккумуляторная отвертка update")
                .available(false)
                .requestId(null)
                .build();

        service.updateItem(itemForUpdateDto, 2000L, 4000L);

        var updatedItem = service.getItem(2000L, 4000L);

        Assertions.assertEquals("Отвертка", currentItem.getName());
        Assertions.assertEquals("Аккумуляторная отвертка", currentItem.getDescription());

        Assertions.assertEquals("Отвертка update", updatedItem.getName());
        Assertions.assertEquals("Аккумуляторная отвертка update", updatedItem.getDescription());
        Assertions.assertEquals(false, updatedItem.getAvailable());
    }

    @Test
    @Order(7)
    void getItemNoItemBehavior() {
        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            service.getItem(999L, 1000L);
        });

        Assertions.assertTrue(ex.getMessage().contains("не найден в базе данных"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @Order(8)
    void createItemStandardBehavior() {
        ItemDto newItemDto = ItemDto.builder()
                .id(null)
                .name("Отвертка update")
                .description("Аккумуляторная отвертка update")
                .available(false)
                .requestId(null)
                .build();

        var retItemDto = service.createItem(newItemDto, 6000L);
        Assertions.assertNotNull(retItemDto.getId());

        var createdItem = service.getItem(retItemDto.getId(), 4000L);

        Assertions.assertEquals("Отвертка update", createdItem.getName());
        Assertions.assertEquals("Аккумуляторная отвертка update", createdItem.getDescription());
        Assertions.assertEquals(false, createdItem.getAvailable());
    }

    @Test
    @Order(9)
    void createItemNoOwnerBehavior() {
        ItemDto newItemDto = ItemDto.builder()
                .id(null)
                .name("Отвертка update")
                .description("Аккумуляторная отвертка update")
                .available(true)
                .requestId(null)
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            service.createItem(newItemDto, 6001L);
        });

        Assertions.assertTrue(ex.getMessage().contains("не найден в базе данных"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}