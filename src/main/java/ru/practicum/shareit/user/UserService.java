package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    List<UserDto> getUsersList();

    UserDto createUser(UserDto userDto);

    void deleteUser(Long userId);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto getUser(Long userId);
}
