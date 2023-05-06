package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    private Integer id;

    @Size(max = 200, message = "Максимальная длина строки с описанием — не более 200 символов")
    private String description;

    private Integer requestor;

    private LocalDate created;
}

