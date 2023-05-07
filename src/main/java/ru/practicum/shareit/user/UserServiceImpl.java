package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserInMemoryStorage userStorage;

    @Autowired
    public UserServiceImpl(UserInMemoryStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<UserDto> getUsersList() {
        var usersList = userStorage.getUsersList();
        return usersList.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = UserMapper.fromDto(userDto);
        return UserMapper.toDto(userStorage.createUser(newUser));
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        validateUserId(userId);

        User userForUpdate = UserMapper.fromDto(userDto);
        userForUpdate.setId(userId);
        return UserMapper.toDto(userStorage.updateUser(userForUpdate));
    }

    @Override
    public UserDto getUser(Long userId) {
        validateUserId(userId);

        User user = userStorage.getUser(userId);
        return UserMapper.toDto(user);
    }

    private void validateUserId(Long uid) {
        if (uid <= 0L) {
            log.error("Объект не может быть сохранен. Причина 'ID должен быть положительным числом больше 0'");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка обновления объекта. ID должен быть положительным числом больше 0");
        }
    }
}