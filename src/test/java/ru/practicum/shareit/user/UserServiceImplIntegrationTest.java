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
@Sql({"/test_schema.sql"})
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
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
        UserDto testUserDto_2 = UserDto.builder()
                .id(null)
                .name("intTest testUserName 2")
                .email("test_user_email_2@mail.com")
                .build();

        service.createUser(testUserDto);
        service.createUser(testUserDto_2);

        var savedUserList = service.getUsersList();

        Assertions.assertEquals(2L, savedUserList.size());
    }

    @Test
    @Order(5)
    void getUserStandardBehavior() {
        service.createUser(testUserDto);

        UserDto savedUser = service.getUser(1L);

        Assertions.assertEquals(1L, savedUser.getId());
        Assertions.assertEquals(testUserDto.getEmail(), savedUser.getEmail());
        Assertions.assertEquals(testUserDto.getName(), savedUser.getName());
    }

    @Test
    @Order(6)
    void updateUserStandardBehavior() {
        service.createUser(testUserDto);

        testUserDto.setName("updatedUsername");

        service.updateUser(testUserDto, 1L);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User updatedUser = query
                .setParameter("id", 1L)
                .getSingleResult();


        Assertions.assertEquals(1L, updatedUser.getId());
        Assertions.assertEquals(testUserDto.getEmail(), updatedUser.getEmail());
        Assertions.assertEquals(testUserDto.getName(), updatedUser.getName());
    }
}