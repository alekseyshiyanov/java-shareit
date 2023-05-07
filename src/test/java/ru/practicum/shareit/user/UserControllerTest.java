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

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/users";
    }

    @Test
    @DirtiesContext
    void getUserStandardBehavior() {
        UserDto testUserDto_1 = UserDto.builder()
                .id(1L)
                .email("tu1@mail.ru")
                .name("Test User 1 Name")
                .build();

        UserDto testUserDto_2 = UserDto.builder()
                .id(2L)
                .email("tu2@mail.ru")
                .name("Test User 2 Name")
                .build();

        UserDto retUser_3 = restTemplate.postForObject(url, testUserDto_1, UserDto.class);
        assertEquals(testUserDto_1, retUser_3);

        UserDto retUser_4 = restTemplate.postForObject(url, testUserDto_2, UserDto.class);
        assertEquals(testUserDto_2, retUser_4);

        String rUrl_1 = url + "/1";
        ResponseEntity<String> actualResponseEntity_1 = restTemplate.exchange(rUrl_1, HttpMethod.GET, null, String.class);

        assertEquals(200, actualResponseEntity_1.getStatusCodeValue());

        String rUrl_2 = url + "/2";
        ResponseEntity<String> actualResponseEntity_2 = restTemplate.exchange(rUrl_2, HttpMethod.GET, null, String.class);

        assertEquals(200, actualResponseEntity_2.getStatusCodeValue());
    }

    @Test
    @DirtiesContext
    void getUserBadIdBehavior() {
        UserDto testUserDto_3 = UserDto.builder()
                .id(1L)
                .email("tu3@mail.ru")
                .name("Test User 3 Name")
                .build();

        UserDto testUserDto_4 = UserDto.builder()
                .id(2L)
                .email("tu4@mail.ru")
                .name("Test User 4 Name")
                .build();

        UserDto retUser_4 = restTemplate.postForObject(url, testUserDto_3, UserDto.class);
        assertEquals(testUserDto_3, retUser_4);

        UserDto retUser_5 = restTemplate.postForObject(url, testUserDto_4, UserDto.class);
        assertEquals(testUserDto_4, retUser_5);

        String rUrl_1 = url + "/1";
        ResponseEntity<String> actualResponseEntity_1 = restTemplate.exchange(rUrl_1, HttpMethod.GET, null, String.class);

        assertEquals(200, actualResponseEntity_1.getStatusCodeValue());

        String rUrl_2 = url + "/2";
        ResponseEntity<String> actualResponseEntity_2 = restTemplate.exchange(rUrl_2, HttpMethod.GET, null, String.class);

        assertEquals(200, actualResponseEntity_2.getStatusCodeValue());

        String rUrl_3 = url + "/3";
        ResponseEntity<String> actualResponseEntity_3 = restTemplate.exchange(rUrl_3, HttpMethod.GET, null, String.class);

        assertEquals(404, actualResponseEntity_3.getStatusCodeValue());

        String responseBody = actualResponseEntity_3.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Пользователь с ID = 3 не существует"));
    }

    @Test
    @DirtiesContext
    void getUserNegativeIdBehavior() {
        UserDto testUserDto_3 = UserDto.builder()
                .id(1L)
                .email("tu3@mail.ru")
                .name("Test User 3 Name")
                .build();

        UserDto testUserDto_4 = UserDto.builder()
                .id(2L)
                .email("tu4@mail.ru")
                .name("Test User 4 Name")
                .build();

        UserDto retUser_4 = restTemplate.postForObject(url, testUserDto_3, UserDto.class);
        assertEquals(testUserDto_3, retUser_4);

        UserDto retUser_5 = restTemplate.postForObject(url, testUserDto_4, UserDto.class);
        assertEquals(testUserDto_4, retUser_5);

        String rUrl_1 = url + "/1";
        ResponseEntity<String> actualResponseEntity_1 = restTemplate.exchange(rUrl_1, HttpMethod.GET, null, String.class);

        assertEquals(200, actualResponseEntity_1.getStatusCodeValue());

        String rUrl_2 = url + "/2";
        ResponseEntity<String> actualResponseEntity_2 = restTemplate.exchange(rUrl_2, HttpMethod.GET, null, String.class);

        assertEquals(200, actualResponseEntity_2.getStatusCodeValue());

        String rUrl_3 = url + "/-3";
        ResponseEntity<String> actualResponseEntity_3 = restTemplate.exchange(rUrl_3, HttpMethod.GET, null, String.class);

        assertEquals(400, actualResponseEntity_3.getStatusCodeValue());

        String responseBody = actualResponseEntity_3.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Ошибка обновления объекта. ID должен быть положительным числом больше 0"));
    }

    @Test
    @DirtiesContext
    void getUserZeroIdBehavior() {
        UserDto testUserDto_3 = UserDto.builder()
                .id(1L)
                .email("tu3@mail.ru")
                .name("Test User 3 Name")
                .build();

        UserDto testUserDto_4 = UserDto.builder()
                .id(2L)
                .email("tu4@mail.ru")
                .name("Test User 4 Name")
                .build();

        UserDto retUser_4 = restTemplate.postForObject(url, testUserDto_3, UserDto.class);
        assertEquals(testUserDto_3, retUser_4);

        UserDto retUser_5 = restTemplate.postForObject(url, testUserDto_4, UserDto.class);
        assertEquals(testUserDto_4, retUser_5);

        String rUrl_1 = url + "/1";
        ResponseEntity<String> actualResponseEntity_1 = restTemplate.exchange(rUrl_1, HttpMethod.GET, null, String.class);

        assertEquals(200, actualResponseEntity_1.getStatusCodeValue());

        String rUrl_2 = url + "/2";
        ResponseEntity<String> actualResponseEntity_2 = restTemplate.exchange(rUrl_2, HttpMethod.GET, null, String.class);

        assertEquals(200, actualResponseEntity_2.getStatusCodeValue());

        String rUrl_3 = url + "/0";
        ResponseEntity<String> actualResponseEntity_3 = restTemplate.exchange(rUrl_3, HttpMethod.GET, null, String.class);

        assertEquals(400, actualResponseEntity_3.getStatusCodeValue());

        String responseBody = actualResponseEntity_3.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Ошибка обновления объекта. ID должен быть положительным числом больше 0"));
    }
}