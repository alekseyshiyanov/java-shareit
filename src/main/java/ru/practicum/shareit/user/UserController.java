package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

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
    public UserDto createUser(@RequestBody @Validated(Create.class) UserDto userDto) {
        log.info("Запрос на создание новой записи");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody @Validated(Update.class) UserDto userDto, @PathVariable("id") Long userId) {
        log.info("Запрос на обновление текущей записи");
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        log.info("Запрос на удаление записи с id = {}", userId);
        userService.deleteUser(userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errorMap = new ArrayList<>();

        for (FieldError err : e.getBindingResult().getFieldErrors()) {
            errorMap.add(err.getDefaultMessage());
        }

        return errorMap;
    }
}
