package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ApiErrorException;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsersList() {
        return UserMapper.toDto(userRepository.findAll());
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
        User userForUpdate = UserMapper.fromDto(userDto);
        userForUpdate.setId(userId);

        userRepository.updateUser(userForUpdate);
        return UserMapper.toDto(userRepository.getReferenceById(userId));
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.getUserById(userId).orElseThrow(() ->
                sendErrorMessage(HttpStatus.NOT_FOUND,
                        "Пользователь с ID = " + userId + " не найден в базе данных"));

        return UserMapper.toDto(user);
    }

    private ApiErrorException sendErrorMessage(HttpStatus httpStatus, String msg) {
        log.error(msg);
        return new ApiErrorException(httpStatus, msg);
    }
}
