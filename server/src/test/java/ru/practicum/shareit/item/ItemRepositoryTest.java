package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/test_schema.sql")
class ItemRepositoryTest {

    @Autowired
    private final TestEntityManager em;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final UserRepository userRepository;

    List<User> testUserList = new ArrayList<>();

    int testUserIndex = 0;
    int testItemIndex = 0;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(itemRepository);
    }

    @Test
    @Rollback
    void createItemStandardBehavior() {
        createTestUsers(1);

        Item newItem = createItem(testUserList.get(0));

        Assertions.assertNull(newItem.getId());

        Assertions.assertNotNull(itemRepository.save(newItem));
        Assertions.assertNotNull(newItem.getId());
    }

    @Test
    @Rollback
    void getItemByIdStandardBehavior() {
        createTestUsers(2);

        Item newItem1 = createItem(testUserList.get(0));
        Item newItem2 = createItem(testUserList.get(1));

        Assertions.assertNull(newItem1.getId());
        Assertions.assertNull(newItem2.getId());

        Assertions.assertNotNull(itemRepository.save(newItem1));
        Assertions.assertNotNull(itemRepository.save(newItem2));

        var itemOpt = itemRepository.getItemById(newItem1.getId());

        Assertions.assertTrue(itemOpt.isPresent());

        var item = itemOpt.get();

        Assertions.assertEquals(newItem1.getName(), item.getName());
        Assertions.assertEquals(newItem1.getDescription(), item.getDescription());
        Assertions.assertEquals(newItem1.getUser(), item.getUser());
    }

    @Test
    @Rollback
    void getItemsByUser_IdStandardBehavior() {
        createTestUsers(3);

        Item newItem1 = createItem(testUserList.get(0));
        Item newItem2 = createItem(testUserList.get(1));
        Item newItem3 = createItem(testUserList.get(0));

        Assertions.assertNotNull(itemRepository.save(newItem1));
        Assertions.assertNotNull(itemRepository.save(newItem2));
        Assertions.assertNotNull(itemRepository.save(newItem3));

        var itemList = itemRepository.getItemsByUser_IdOrderByIdAsc(testUserList.get(0).getId());
        Assertions.assertNotEquals(0, itemList.size());
        Assertions.assertTrue(itemList.get(0).getId() < itemList.get(1).getId());
    }

    @Test
    @Rollback
    void searchItemsByDescriptionOrNameStandardBehavior() {
        createTestUsers(3);

        Item newItem1 = createItem(testUserList.get(0));
        Item newItem2 = createItem(testUserList.get(1));
        Item newItem3 = createItem(testUserList.get(1));

        newItem1.setAvailable(true);

        Assertions.assertNotNull(itemRepository.save(newItem1));
        Assertions.assertNotNull(itemRepository.save(newItem2));
        Assertions.assertNotNull(itemRepository.save(newItem3));

        var item0 = itemRepository.searchItemsByDescriptionOrName(newItem1.getDescription().toLowerCase());
        var item1 = itemRepository.searchItemsByDescriptionOrName(newItem2.getName().toLowerCase());

        Assertions.assertEquals(1, item0.size());
        Assertions.assertEquals(0, item1.size());

        Assertions.assertEquals(newItem1.getDescription(), item0.get(0).getDescription());
    }

    @Test
    @Rollback
    void updateItemStandardBehavior() {
        String newDescription = "updated item description";

        createTestUsers(1);

        Item newItem1 = createItem(testUserList.get(0));

        Assertions.assertNotNull(itemRepository.save(newItem1));
        Assertions.assertNotEquals(newDescription, newItem1.getDescription());

        newItem1.setDescription(newDescription);

        itemRepository.updateItem(newItem1);

        var updatedItemOpt = itemRepository.getItemById(newItem1.getId());
        Assertions.assertTrue(updatedItemOpt.isPresent());

        var updatedItem = updatedItemOpt.get();

        Assertions.assertEquals(newDescription, updatedItem.getDescription());
    }

    private void createTestUsers(int userCount) {
        Assertions.assertTrue(userCount > 0, "Количество тестовых пользователей должно быть больше 0");

        for (; testUserIndex < userCount; testUserIndex++) {
            User testUser = new User();
            testUser.setId(null);
            testUser.setEmail("test_create_" + testUserIndex + "@mail.ru");
            testUser.setName("ItemRepositoryTest BeforeAll Test User " + testUserIndex);

            Assertions.assertNotNull(userRepository.save(testUser));

            testUserList.add(testUser);
        }
    }

    private Item createItem(User user) {
        return Item.builder()
                .id(null)
                .name("Test Item Name " + testItemIndex)
                .description("Test Item Description " + testItemIndex++)
                .available(false)
                .user(user)
                .request(null)
                .build();
    }
}