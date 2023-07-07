package ru.practicum.shareit.request;

import lombok.*;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    private Integer id;

    @Size(max = 200, message = "Максимальная длина строки с описанием — не более 200 символов")
    private String description;

    private Integer requestor;

    private LocalDate created;
}

