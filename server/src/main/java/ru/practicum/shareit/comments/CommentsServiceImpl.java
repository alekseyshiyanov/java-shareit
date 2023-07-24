package ru.practicum.shareit.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ApiErrorException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CommentsServiceImpl implements CommentsService {
    private final CommentsRepository commentsRepository;
    private final BookingRepository bookingRepository;

    public CommentsDto createComment(Long itemId, Long authorId, CommentsDto commentsDto) {
        Booking booking = bookingRepository.getTopBookingByItem_IdAndBooker_IdAndEndBeforeOrderByEndDesc(itemId, authorId, LocalDateTime.now()).orElseThrow(() ->
                sendErrorMessage(HttpStatus.BAD_REQUEST,
                        "Бронирование с ItemID = " + itemId + " и UserID = " + authorId + " не найдено в базе данных или еще не завершено"));

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

    private ApiErrorException sendErrorMessage(HttpStatus httpStatus, String msg) {
        log.error(msg);
        return new ApiErrorException(httpStatus, msg);
    }
}
