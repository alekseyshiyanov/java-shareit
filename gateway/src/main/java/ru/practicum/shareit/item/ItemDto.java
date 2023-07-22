package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.labels.Create;
import ru.practicum.shareit.labels.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto implements Create, Update {
    private Long id;

    @NotBlank(groups = Create.class, message = "Название предмета не может быть пустым или равно null")
    private String name;

    @NotBlank(groups = Create.class, message = "Описание предмета не может быть пустым или равно null")
    private String description;

    @NotNull(groups = Create.class, message = "Доступность предмета не может быть null")
    private Boolean available;

    private Integer requestId;
}
