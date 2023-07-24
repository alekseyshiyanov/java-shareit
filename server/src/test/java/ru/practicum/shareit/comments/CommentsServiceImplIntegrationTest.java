package ru.practicum.shareit.comments;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql({
        "/test_schema.sql",
        "/import_user_data.sql",
        "/import_item_request_data.sql",
        "/import_item_data.sql",
        "/import_booking_data.sql",
        "/import_comments_data.sql"
})
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentsServiceImplIntegrationTest {

    private final CommentsService commentsService;
    private final EntityManager em;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(commentsService);
        Assertions.assertNotNull(em);
    }


    @Test
    @Order(1)
    void createCommentStandardBehavior() {
        Query getAllCommentsQuery = em.createNativeQuery("SELECT * FROM public.comments");
        var initialCommentList = getAllCommentsQuery.getResultList();

        var newCommentDto = CommentsDto.builder()
                .id(null)
                .text("new Comment")
                .authorName(null)
                .created(LocalDateTime.now())
                .build();

        commentsService.createComment(2000L, 5000L, newCommentDto);

        var newCommentList = getAllCommentsQuery.getResultList();

        Assertions.assertTrue(newCommentList.size() > initialCommentList.size());
    }
}