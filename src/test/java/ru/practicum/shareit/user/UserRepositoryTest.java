package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
    void updateUserStandardBehavior() {
        User testUser = User.builder()
                .id(null)
                .email("test_create@mail.ru")
                .name("updateUserStandardBehaviorFirst")
                .build();

        Assertions.assertNull(testUser.getId());
        User ret = userRepository.save(testUser);
        Assertions.assertNotNull(ret);
        Assertions.assertNotNull(ret.getId());

        User updateUser = User.builder()
                .id(ret.getId())
                .email("test_update@mail.ru")
                .name("createUserStandardBehaviorUpdate")
                .build();
        userRepository.updateUser(updateUser);

        var updatedUserOpt = userRepository.getUserById(ret.getId());
        Assertions.assertTrue(updatedUserOpt.isPresent());

        var updatedUser = updatedUserOpt.get();

        Assertions.assertNotNull(updatedUser.getId());

        Assertions.assertEquals(updateUser.getName(), updatedUser.getName());
        Assertions.assertEquals(updateUser.getEmail(), updatedUser.getEmail());
    }
}