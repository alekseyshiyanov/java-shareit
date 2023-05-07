package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url;

    private UserDto testUserDto1;
    private UserDto testUserDto2;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/users";
    }

    @Test
    @DirtiesContext
    void getUserStandardBehavior() {
        createTestUsers();

        createUserTest();

        String rUrl1 = url + "/1";
        ResponseEntity<String> actualResponseEntity1 = restTemplate.exchange(rUrl1, HttpMethod.GET, null, String.class);

        assertEquals(200, actualResponseEntity1.getStatusCodeValue());

        String rUrl2 = url + "/2";
        ResponseEntity<String> actualResponseEntity2 = restTemplate.exchange(rUrl2, HttpMethod.GET, null, String.class);

        assertEquals(200, actualResponseEntity2.getStatusCodeValue());
    }

    @Test
    @DirtiesContext
    void getUserBadIdBehavior() {
        createTestUsers();

        createUserTest();

        String rUrl = url + "/3";
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(rUrl, HttpMethod.GET, null, String.class);

        assertEquals(404, actualResponseEntity.getStatusCodeValue());

        String responseBody = actualResponseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Пользователь с ID = 3 не существует"));
    }

    @Test
    @DirtiesContext
    void getUserNegativeIdBehavior() {
        createTestUsers();

        createUserTest();

        String rUrl = url + "/-1";
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(rUrl, HttpMethod.GET, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());

        String responseBody = actualResponseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Ошибка обновления объекта. ID должен быть положительным числом больше 0"));
    }

    @Test
    @DirtiesContext
    void getUserZeroIdBehavior() {
        createTestUsers();

        createUserTest();

        String rUrl = url + "/0";
        ResponseEntity<String> actualResponseEntity = restTemplate.exchange(rUrl, HttpMethod.GET, null, String.class);

        assertEquals(400, actualResponseEntity.getStatusCodeValue());

        String responseBody = actualResponseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Ошибка обновления объекта. ID должен быть положительным числом больше 0"));
    }

    private void createTestUsers() {
        testUserDto1 = UserDto.builder()
                .id(1L)
                .email("tu1@mail.ru")
                .name("Test User 1 Name")
                .build();

        testUserDto2 = UserDto.builder()
                .id(2L)
                .email("tu2@mail.ru")
                .name("Test User 2 Name")
                .build();
    }

    private void createUserTest() {
        UserDto retUser1 = restTemplate.postForObject(url, testUserDto1, UserDto.class);
        assertEquals(testUserDto1, retUser1);

        UserDto retUser2 = restTemplate.postForObject(url, testUserDto2, UserDto.class);
        assertEquals(testUserDto2, retUser2);
    }
}