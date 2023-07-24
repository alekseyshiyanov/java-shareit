package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.labels.Create;
import ru.practicum.shareit.labels.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsersList() {
        log.info("Запрос на получение списка пользователей");
        return userClient.getUsersList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@Valid @PathVariable("id")
                                                 @Positive(message = "Значение 'userId' должно быть положительным числом больше 0") Long userId) {
        log.info("Запрос на получение данных пользователя с ID={}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(Create.class) UserDto userDto) {
        log.info("Запрос на создание новой записи");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated(Update.class) UserDto userDto,
                                             @Valid @PathVariable("id")
                                                    @Positive(message = "Значение 'userId' должно быть положительным числом больше 0") Long userId) {
        log.info("Запрос на обновление записи с id = {}", userId);
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@Valid @PathVariable("id")
                                                    @Positive(message = "Значение 'userId' должно быть положительным числом больше 0") Long userId) {
        log.info("Запрос на удаление записи с id = {}", userId);
        return userClient.deleteUser(userId);
    }
}
