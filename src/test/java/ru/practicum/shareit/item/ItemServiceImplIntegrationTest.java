package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

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
class ItemServiceImplIntegrationTest {

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
}