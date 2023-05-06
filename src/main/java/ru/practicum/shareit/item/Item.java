package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private Integer owner;

    private Integer request;
}
