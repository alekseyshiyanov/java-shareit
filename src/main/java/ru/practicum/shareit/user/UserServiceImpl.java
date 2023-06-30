package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getUsersList() {
        var usersList = userRepository.findAll();
        return usersList.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = UserMapper.fromDto(userDto);
        return UserMapper.toDto(userRepository.save(newUser));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUserById(userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        validateUserId(userId);

        User userForUpdate = UserMapper.fromDto(userDto);
        userForUpdate.setId(userId);

        userRepository.updateUser(userForUpdate);
        return UserMapper.toDto(userRepository.getReferenceById(userId));
    }

    @Override
    public UserDto getUser(Long userId) {
        validateUserId(userId);

        User user = userRepository.getUserById(userId);

        if (user != null) {
            return UserMapper.toDto(user);
        }

        log.error("Пользователь с ID = {} не найден в базе данных", userId);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID = " + userId + " не найден в базе данных");
    }

    private void validateUserId(Long uid) {
        if (uid <= 0L) {
            log.error("Объект не может быть сохранен. Причина 'ID должен быть положительным числом больше 0'");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка обновления объекта. ID должен быть положительным числом больше 0");
        }
    }
}
