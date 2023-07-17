package ru.practicum.shareit.comments;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exceptions.ApiErrorException;

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
class CommentsServiceImplUnitTest {

    private final CommentsService commentsService;
    private final EntityManager em;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(commentsService);
        Assertions.assertNotNull(em);
    }

    @Test
    @Order(2)
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

    @Test
    @Order(3)
    void createCommentNullDtoBehavior() {
        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            commentsService.createComment(2000L, 5000L, null);
        });

        Assertions.assertTrue(ex.getMessage().contains("Комментарий не может быть пустым или равным null"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(4)
    void createCommentNullTextBehavior() {
        var newCommentDto = CommentsDto.builder()
                .id(null)
                .text(null)
                .authorName(null)
                .created(LocalDateTime.now())
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            commentsService.createComment(2000L, 5000L, newCommentDto);
        });

        Assertions.assertTrue(ex.getMessage().contains("Комментарий не может быть пустым или равным null"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(5)
    void createCommentEmptyTextBehavior() {
        var newCommentDto = CommentsDto.builder()
                .id(null)
                .text("")
                .authorName(null)
                .created(LocalDateTime.now())
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            commentsService.createComment(2000L, 5000L, newCommentDto);
        });

        Assertions.assertTrue(ex.getMessage().contains("Комментарий не может быть пустым или равным null"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(6)
    void createCommentBlankTextBehavior() {
        var newCommentDto = CommentsDto.builder()
                .id(null)
                .text("  ")
                .authorName(null)
                .created(LocalDateTime.now())
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            commentsService.createComment(2000L, 5000L, newCommentDto);
        });

        Assertions.assertTrue(ex.getMessage().contains("Комментарий не может быть пустым или равным null"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(7)
    void createCommentNoItemBehavior() {
        var newCommentDto = CommentsDto.builder()
                .id(null)
                .text("New text")
                .authorName(null)
                .created(LocalDateTime.now())
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            commentsService.createComment(5000L, 5000L, newCommentDto);
        });

        Assertions.assertTrue(ex.getMessage().contains("Бронирование с ItemID = 5000 и UserID = 5000 не найдено в базе данных или еще не завершено"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(8)
    void createCommentNoUserBehavior() {
        var newCommentDto = CommentsDto.builder()
                .id(null)
                .text("New text")
                .authorName(null)
                .created(LocalDateTime.now())
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            commentsService.createComment(2000L, 6000L, newCommentDto);
        });

        Assertions.assertTrue(ex.getMessage().contains("Бронирование с ItemID = 2000 и UserID = 6000 не найдено в базе данных или еще не завершено"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @Order(9)
    void createCommentCurrentBookingBehavior() {
        var newCommentDto = CommentsDto.builder()
                .id(null)
                .text("New text")
                .authorName(null)
                .created(LocalDateTime.now())
                .build();

        ApiErrorException ex = Assertions.assertThrows(ApiErrorException.class, () -> {
            commentsService.createComment(1000L, 5000L, newCommentDto);
        });

        Assertions.assertTrue(ex.getMessage().contains("Бронирование с ItemID = 1000 и UserID = 5000 не найдено в базе данных или еще не завершено"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

}