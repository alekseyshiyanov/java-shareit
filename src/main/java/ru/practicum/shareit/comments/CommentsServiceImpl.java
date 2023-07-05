package ru.practicum.shareit.comments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ApiErrorException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class CommentsServiceImpl implements CommentsService {
    private final CommentsRepository commentsRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public CommentsServiceImpl(CommentsRepository commentsRepository,
                               BookingRepository bookingRepository) {
        this.commentsRepository = commentsRepository;
        this.bookingRepository = bookingRepository;
    }

    public CommentsDto createComment(Long itemId, Long authorId, CommentsDto commentsDto) {
        if (commentsDto == null || commentsDto.getText() == null || commentsDto.getText().isBlank()) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Комментарий не может быть пустым или равным null");
        }

        Booking booking = bookingRepository.getTopBookingByItem_IdAndBooker_IdAndEndBeforeOrderByEndDesc(itemId, authorId, LocalDateTime.now());

        if (booking == null) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Бронирование с ItemID = " + itemId
                    + " и UserID = " + authorId + " не найдено в базе данных");
        }

        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            sendErrorMessage(HttpStatus.BAD_REQUEST, "Бронирование с предмета с ItemID = " + itemId
                    + " еще не завершено");
        }

        Comments newComment = Comments.builder()
                                        .id(null)
                                        .text(commentsDto.getText())
                                        .item(booking.getItem())
                                        .author(booking.getBooker())
                                        .created(LocalDateTime.now())
                                        .build();

        Comments ret = commentsRepository.save(newComment);

        return CommentsMapper.toDto(ret);
    }

    private void sendErrorMessage(HttpStatus httpStatus, String msg) {
        log.error(msg);
        throw new ApiErrorException(httpStatus, msg);
    }
}
