package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsersList() {
        log.info("Запрос на получение списка пользователей");
        return userService.getUsersList();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long userId) {
        log.info("Запрос на получение данных пользователя с ID={}", userId);
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Запрос на создание новой записи");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                              @PathVariable("id") Long userId) {
        log.info("Запрос на обновление текущей записи");
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        log.info("Запрос на удаление записи с id = {}", userId);
        userService.deleteUser(userId);
    }
}
