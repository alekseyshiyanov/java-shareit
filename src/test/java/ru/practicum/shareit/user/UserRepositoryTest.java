package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/test_schema.sql")
public class UserRepositoryTest {

    @Autowired
    private final TestEntityManager em;

    @Autowired
    private final UserRepository userRepository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
        Assertions.assertNotNull(userRepository);
    }

    @Test
    @Rollback
    void createUserStandardBehavior() {
        User testUser = User.builder()
                .id(null)
                .email("test_create@mail.ru")
                .name("createUserStandardBehavior")
                .build();

        Assertions.assertNull(testUser.getId());
        User ret = userRepository.save(testUser);
        Assertions.assertNotNull(ret);
        Assertions.assertNotNull(ret.getId());
        Assertions.assertEquals(testUser.getName(), ret.getName());
        Assertions.assertEquals(testUser.getEmail(), ret.getEmail());
    }

    @Test
    @Sql("/import_user_data.sql")
    @Rollback
    void updateUserStandardBehavior() {
        var userForUpdateOpt = userRepository.getUserById(1000L);

        Assertions.assertTrue(userForUpdateOpt.isPresent(), "Пользователь с ID = 1000 не существует в тестовой базе данных");

        var userForUpdate = userForUpdateOpt.get();

        Assertions.assertEquals("user1 name", userForUpdate.getName());
        Assertions.assertEquals("user_1@user.com", userForUpdate.getEmail());

        userForUpdate.setName("update User Name");
        userForUpdate.setEmail("user_1_updated_0@user.com");

        userRepository.updateUser(userForUpdate);

        var updatedUserOpt = userRepository.getUserById(1000L);
        Assertions.assertTrue(updatedUserOpt.isPresent(), "Пользователь с ID = 1000 не существует в тестовой базе данных");

        var updatedUser = updatedUserOpt.get();

        Assertions.assertNotNull(updatedUser.getId());

        Assertions.assertEquals("update User Name", updatedUser.getName());
        Assertions.assertEquals("user_1_updated_0@user.com", updatedUser.getEmail());
    }
}