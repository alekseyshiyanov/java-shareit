package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exceptions.ApiErrorException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

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
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    ItemRequestService service;

    @Autowired
    EntityManager entityManager;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(service);
        Assertions.assertNotNull(entityManager);
    }

    @Test
    @Order(2)
    void createItemRequestStandardBehavior() {
        var initialSize = service.getAllItemRequestByUser(1000L).size();

        ItemRequestDto irDto = ItemRequestDto.builder()
                .id(null)
                .description("Дайте клей Момент")
                .requester(null)
                .items(null)
                .created(LocalDateTime.now())
                .build();

        service.createItemRequest(irDto,1000L);

        var newSize = service.getAllItemRequestByUser(1000L).size();
        Assertions.assertTrue(newSize > initialSize);
    }

    @Test
    @Order(3)
    void createItemRequestNoUserBehavior() {
        ItemRequestDto irDto = ItemRequestDto.builder()
                .id(null)
                .description("Дайте клей Момент")
                .requester(null)
                .items(null)
                .created(LocalDateTime.now())
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () ->
                service.createItemRequest(irDto,10000L));

        Assertions.assertTrue(ex.getMessage().contains("не найден в базе данных"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}