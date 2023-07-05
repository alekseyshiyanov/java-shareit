package ru.practicum.shareit.comments;

import javax.transaction.Transactional;

@Transactional
public interface CommentsService {
    CommentsDto createComment(Long itemId, Long authorId, CommentsDto commentsDto);
}
