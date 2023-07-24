package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({
        "/test_schema.sql",
        "/import_user_data.sql",
        "/import_item_request_data.sql",
        "/import_item_data.sql",
        "/import_booking_data.sql",
        "/import_comments_data.sql"
})
class UserServiceImplIntegrationTest {

    private final UserService service;
    private final EntityManager entityManager;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = UserDto.builder()
                .id(null)
                .name("intTest testUserName 1")
                .email("test_user_email_1@mail.com")
                .build();
    }

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(service);
        Assertions.assertNotNull(entityManager);
    }

    @Test
    @Order(2)
    void createUserStandardBehavior() {
        service.createUser(testUserDto);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User retUser = query
                            .setParameter("email", testUserDto.getEmail())
                            .getSingleResult();

        Assertions.assertNotNull(retUser.getId());
        Assertions.assertEquals(testUserDto.getEmail(), retUser.getEmail());
        Assertions.assertEquals(testUserDto.getName(), retUser.getName());
    }

    @Test
    @Order(3)
    void deleteUserStandardBehavior() {
        service.createUser(testUserDto);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User retUser = query
                .setParameter("email", testUserDto.getEmail())
                .getSingleResult();

        service.deleteUser(retUser.getId());

        Exception ex = Assertions.assertThrows(NoResultException.class, () ->
                query.setParameter("email", testUserDto.getEmail()).getSingleResult());

        Assertions.assertTrue(ex.getMessage().contains("No entity found for query"));
    }

    @Test
    @Order(4)
    void getUserListStandardBehavior() {
        var savedUserList = service.getUsersList();

        Assertions.assertEquals(4L, savedUserList.size());
    }

    @Test
    @Order(5)
    void getUserStandardBehavior() {
        UserDto savedUser = service.getUser(1000L);

        Assertions.assertEquals(1000L, savedUser.getId());
        Assertions.assertEquals("user1 name", savedUser.getName());
        Assertions.assertEquals("user_1@user.com", savedUser.getEmail());
    }

    @Test
    @Order(6)
    void updateUserStandardBehavior() {
        testUserDto.setName("updatedUsername");

        service.updateUser(testUserDto, 1000L);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User updatedUser = query
                .setParameter("id", 1000L)
                .getSingleResult();


        Assertions.assertEquals(1000L, updatedUser.getId());
        Assertions.assertEquals(testUserDto.getEmail(), updatedUser.getEmail());
        Assertions.assertEquals(testUserDto.getName(), updatedUser.getName());
    }
}