package ru.practicum.shareit.user;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> toDto(List<User> usersList) {
        if (usersList == null) {
            return Collections.emptyList();
        }

        return usersList.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public static User fromDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }
}
