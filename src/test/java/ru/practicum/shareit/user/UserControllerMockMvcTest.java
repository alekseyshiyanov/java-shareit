package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exceptions.handlers.RestApiExceptionHandler;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerMockMvcTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(RestApiExceptionHandler.class)
                .build();
    }

    @Test
    void createNewUser() throws Exception {
        UserDto testUser = createUserDto(1L);

        when(userService.createUser(any(UserDto.class))).thenReturn(testUser);

        mvc.perform(
                post("/users")
                        .content(mapper.writeValueAsString(testUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testUser.getName())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())));
    }

    @Test
    void getUser() throws Exception {
        UserDto testUser = createUserDto(1L);

        when(userService.getUser(any(Long.class))).thenReturn(testUser);

        Long userId = testUser.getId();

        mvc.perform(
                get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name", is(testUser.getName())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        UserDto testUser = createUserDto(1L);

        Long userId = testUser.getId();

        mvc.perform(
                delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void updateUser() throws Exception {
        UserDto testUser = createUserDto(1L);

        when(userService.updateUser(any(UserDto.class), any(Long.class))).thenReturn(testUser);

        Long userId = testUser.getId();

        mvc.perform(
                patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(testUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name", is(testUser.getName())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())));
    }

    @Test
    void getUsersList() throws Exception {
        List<UserDto> testUserDtoList = Arrays.asList(
                createUserDto(1L),
                createUserDto(2L),
                createUserDto(3L),
                createUserDto(4L)
        );

        when(userService.getUsersList()).thenReturn(testUserDtoList);

        MvcResult result = mvc.perform(
                        get("/users")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        List<UserDto> actualUserDtoList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        Assertions.assertEquals(testUserDtoList.size(), actualUserDtoList.size());
        Assertions.assertEquals(testUserDtoList, actualUserDtoList);
    }

    private UserDto createUserDto(Long id) {
        return UserDto.builder()
                .id(id)
                .name("TestUserName_" + id)
                .email("test_user_email_" + id + "@mail.com")
                .build();
    }
}