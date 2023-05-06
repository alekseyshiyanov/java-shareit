package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String name;

    @NotNull(message = "Проверьте правильность ввода адреса электронной почты: не может быть null")
    @Email(message = "Проверьте правильность ввода адреса электронной почты")
    private String email;
}
