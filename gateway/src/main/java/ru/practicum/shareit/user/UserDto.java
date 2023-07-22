package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.labels.Create;
import ru.practicum.shareit.labels.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Create, Update {
    private Long id;

    private String name;

    @NotEmpty(groups = Create.class, message = "Проверьте правильность ввода адреса электронной почты: не может быть пустым или равен null")
    @Email(groups = {Update.class, Create.class}, message = "Проверьте правильность ввода адреса электронной почты")
    private String email;
}
