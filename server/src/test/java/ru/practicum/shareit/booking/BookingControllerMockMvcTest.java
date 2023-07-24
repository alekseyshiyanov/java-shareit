package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.exceptions.handlers.RestApiExceptionHandler;

import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerMockMvcTest {
    @Mock
    private BookingService service;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(RestApiExceptionHandler.class)
                .build();

        mapper.findAndRegisterModules();
    }

    @Test
    void createBooking() throws Exception {
        Long bookerId = 1L;

        BookingDto testBookingDto = createBookingDto(1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                BookingStatus.WAITING);

        InputBookingDto testInputBookingDto = createInputBookingDto(testBookingDto);

        when(service.createBooking(any(InputBookingDto.class), any(Long.class))).thenReturn(testBookingDto);

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", bookerId)
                                .content(mapper.writeValueAsString(testInputBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(testBookingDto.getStatus().name())));
    }

    @Test
    void approvalBooking() throws Exception {
        Long bookerId = 1L;
        Boolean approved = true;
        Long bookingId = 1L;

        BookingDto testBookingDto = createBookingDto(1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                BookingStatus.APPROVED);

        when(service.approvalBooking(any(Long.class), any(Long.class), any(Boolean.class))).thenReturn(testBookingDto);

        mvc.perform(
                        patch("/bookings/{bookingId}", bookingId)
                                .header("X-Sharer-User-Id", bookerId)
                                .param("approved", String.valueOf(approved))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(testBookingDto.getStatus().name())));
    }

    @Test
    void getBookingByUser() throws Exception {
        Long bookerId = 1L;
        Long bookingId = 1L;

        BookingDto testBookingDto = createBookingDto(1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                BookingStatus.APPROVED);

        when(service.getBookingByUser(any(Long.class), any(Long.class))).thenReturn(testBookingDto);

        mvc.perform(
                        get("/bookings/{bookingId}", bookingId)
                                .header("X-Sharer-User-Id", bookerId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(testBookingDto.getStatus().name())));
    }

    @Test
    void getAllBookingByUser() throws Exception {
        Long bookerId = 1L;
        Integer from = 0;
        Integer size = 0;
        String state = "ALL";

        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();

        reqParam.add("state", state);
        reqParam.add("from", from.toString());
        reqParam.add("size", size.toString());


        List<BookingDto> testBookingDtoList = Arrays.asList(
                createBookingDto(1L,LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2),BookingStatus.APPROVED));

        when(service.getAllBookingByUser(any(Long.class), any(Integer.class), any(Integer.class), any(String.class))).thenReturn(testBookingDtoList);

        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", bookerId)
                                .params(reqParam)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(testBookingDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(testBookingDtoList.get(0).getStatus().name())));
    }

    @Test
    void getAllBookingByOwner() throws Exception {
        Long bookerId = 1L;
        Integer from = 0;
        Integer size = 0;
        String state = "ALL";

        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();

        reqParam.add("state", state);
        reqParam.add("from", from.toString());
        reqParam.add("size", size.toString());


        List<BookingDto> testBookingDtoList = Arrays.asList(
                createBookingDto(1L,LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2),BookingStatus.APPROVED));

        when(service.getAllBookingByOwner(any(Long.class), any(Integer.class), any(Integer.class), any(String.class))).thenReturn(testBookingDtoList);

        mvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", bookerId)
                                .params(reqParam)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(testBookingDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(testBookingDtoList.get(0).getStatus().name())));
    }



    private BookingDto createBookingDto(Long id, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        return BookingDto.builder()
                .id(id)
                .start(start)
                .end(end)
                .item(null)
                .booker(null)
                .status(status)
                .build();
    }

    private InputBookingDto createInputBookingDto(BookingDto inputData) {
        return InputBookingDto.builder()
                .itemId(inputData.getId())
                .start(inputData.getStart())
                .end(inputData.getEnd())
                .build();
    }
}