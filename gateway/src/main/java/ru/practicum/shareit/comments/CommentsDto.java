package ru.practicum.shareit.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentsDto {
    Long id;

    @NotBlank(message = "Комментарий не может быть пустым или равным null")
    String text;

    String authorName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime created;
}
