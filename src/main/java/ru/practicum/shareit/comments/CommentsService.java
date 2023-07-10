package ru.practicum.shareit.comments;

public interface CommentsService {
    CommentsDto createComment(Long itemId, Long authorId, CommentsDto commentsDto);
}
