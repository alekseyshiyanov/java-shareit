package ru.practicum.shareit.request;

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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerMockMvcTest {
    @Mock
    private ItemRequestService service;

    @InjectMocks
    private ItemRequestController controller;

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
    void createItemRequest() throws Exception {
        Long userId = 1L;
        var testItemRequestDto = createItemRequestDto(1L, LocalDateTime.now());

        when(service.createItemRequest(any(ItemRequestDto.class), any(Long.class))).thenReturn(testItemRequestDto);

        mvc.perform(
                        post("/requests")
                                .header("X-Sharer-User-Id", userId)
                                .content(mapper.writeValueAsString(testItemRequestDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(testItemRequestDto.getDescription())));
    }

    @Test
    void getAllItemRequest() throws Exception {
        Long userId = 1L;
        var testItemRequestDtoList = Arrays.asList(createItemRequestDto(1L, LocalDateTime.now()));

        when(service.getAllItemRequestByUser(any(Long.class))).thenReturn(testItemRequestDtoList);

        mvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", userId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(testItemRequestDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(testItemRequestDtoList.get(0).getDescription())));
    }

    @Test
    void getAllItemRequestByIdAndUser() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;

        var testItemRequestDto = createItemRequestDto(1L, LocalDateTime.now());

        when(service.getAllItemRequestByIdAndUser(any(Long.class), any(Long.class))).thenReturn(testItemRequestDto);

        var res = mvc.perform(
                        get("/requests/{requestId}", requestId)
                                .header("X-Sharer-User-Id", userId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(testItemRequestDto.getDescription())));
    }

    @Test
    void getPageItemRequestByUser() throws Exception {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 0;

        MultiValueMap<String, String> reqParam = new LinkedMultiValueMap<>();

        reqParam.add("from", from.toString());
        reqParam.add("size", size.toString());

        var testItemRequestDtoList = Arrays.asList(createItemRequestDto(1L, LocalDateTime.now()));

        when(service.getPageItemRequestByUser(any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(testItemRequestDtoList);

        mvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", userId)
                                .params(reqParam)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(testItemRequestDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(testItemRequestDtoList.get(0).getDescription())));
    }

    private ItemRequestDto createItemRequestDto(Long id, LocalDateTime ldt) {
        return ItemRequestDto.builder()
                .id(id)
                .description("ItemRequestDescription_" + id)
                .requester(null)
                .items(null)
                .created(ldt)
                .build();
    }
}
