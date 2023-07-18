package ru.practicum.shareit.comments;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommentsMapper {
    public static List<CommentsDto> toDto(List<Comments> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }

        return comments.stream()
                .map(CommentsMapper::toDto)
                .collect(Collectors.toList());
    }

    public static CommentsDto toDto(Comments comments) {
        if (comments == null) {
            return null;
        }

        return CommentsDto.builder()
                .id(comments.getId())
                .text(comments.getText())
                .authorName(comments.getAuthor().getName())
                .created(comments.getCreated())
                .build();
    }
}
