package ru.practicum.shareit.comments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findCommentsByItem_IdAndAuthor_Id(Long userId, Long authorId);

    List<Comments> findCommentsByItem_Id(Long userId);
}

