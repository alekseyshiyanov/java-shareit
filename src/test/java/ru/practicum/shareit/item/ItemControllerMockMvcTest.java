package ru.practicum.shareit.item;

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
import ru.practicum.shareit.comments.CommentsDto;
import ru.practicum.shareit.comments.CommentsService;
import ru.practicum.shareit.exceptions.handlers.RestApiExceptionHandler;

import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
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
class ItemControllerMockMvcTest {
    @Mock
    private ItemService itemService;

    @Mock
    private CommentsService commentsService;

    @InjectMocks
    private ItemController controller;

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
    void createItem() throws Exception{
        Long ownerId = 1L;
        ItemDto testItemDto = createItemDto(1L, false);

        when(itemService.createItem(any(ItemDto.class), any(Long.class))).thenReturn(testItemDto);

        mvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", ownerId)
                                .content(mapper.writeValueAsString(testItemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testItemDto.getName())))
                .andExpect(jsonPath("$.description", is(testItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(testItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(testItemDto.getRequestId())));
    }

    @Test
    void createComment() throws Exception{
        Long ownerId = 1L;
        Long itemId = 1L;
        LocalDateTime ldt = LocalDateTime.now();

        CommentsDto testCommentDto = createCommentDto(1L, ldt);

        when(commentsService.createComment(any(Long.class), any(Long.class), any(CommentsDto.class))).thenReturn(testCommentDto);

        mvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .header("X-Sharer-User-Id", ownerId)
                                .content(mapper.writeValueAsString(testCommentDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(testCommentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(testCommentDto.getAuthorName())));
    }

    @Test
    void getItem() throws Exception{
        ItemDto testItemDto = createItemDto(1L, false);
        OutItemDto testOutItemDto = createOutItemDto(testItemDto);

        Long ownerId = 1L;
        Long itemId = testItemDto.getId();

        when(itemService.getItem(any(Long.class), any(Long.class))).thenReturn(testOutItemDto);

        mvc.perform(
                        get("/items/{itemId}", itemId)
                                .header("X-Sharer-User-Id", ownerId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testOutItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testOutItemDto.getName())))
                .andExpect(jsonPath("$.description", is(testOutItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(testOutItemDto.getAvailable())))
                .andExpect(jsonPath("$.request", is(testOutItemDto.getRequest())));
    }

    @Test
    void searchItem() throws Exception{
        Long ownerId = 1L;

        List<ItemDto> testItemDtoList = Arrays.asList(
                createItemDto(1L, false)
        );

        String searchString = testItemDtoList.get(0).getName();

        when(itemService.getSearchedItems(any(String.class))).thenReturn(testItemDtoList);

        mvc.perform(
                        get("/items/search")
                                .header("X-Sharer-User-Id", ownerId)
                                .param("text", searchString)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(testItemDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(testItemDtoList.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(testItemDtoList.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(testItemDtoList.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(testItemDtoList.get(0).getRequestId())));
    }

    @Test
    void updateItem() throws Exception{
        Long ownerId = 1L;

        ItemDto testItemDto = createItemDto(1L, false);
        Long itemId = testItemDto.getId();

        when(itemService.updateItem(any(ItemDto.class), any(Long.class), any(Long.class))).thenReturn(testItemDto);

        mvc.perform(
                        patch("/items/{itemId}", itemId)
                                .header("X-Sharer-User-Id", ownerId)
                                .content(mapper.writeValueAsString(testItemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name", is(testItemDto.getName())))
                .andExpect(jsonPath("$.description", is(testItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(testItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(testItemDto.getRequestId())));
    }

    @Test
    void getItemsByOwnerId() throws Exception{
        Long ownerId = 1L;
        ItemDto testItemDto = createItemDto(1L, false);

        List<OutItemDto> testOutItemDtoList = Arrays.asList(
                createOutItemDto(testItemDto)
        );

        when(itemService.getItemsByOwnerId(any(Long.class))).thenReturn(testOutItemDtoList);

        mvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", ownerId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(testOutItemDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(testOutItemDtoList.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(testOutItemDtoList.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(testOutItemDtoList.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].request", is(testOutItemDtoList.get(0).getRequest())));
    }

    private ItemDto createItemDto(Long id, Boolean available) {
        return ItemDto.builder()
                .id(id)
                .name("TestItemName_" + id)
                .description("TestItemDescription_" + id)
                .available(available)
                .requestId(null)
                .build();
    }

    private CommentsDto createCommentDto(Long id, LocalDateTime ldt) {
        return CommentsDto.builder()
                .id(id)
                .text("TestCommentText_" + id)
                .authorName("TestCommentAuthorName_" + id)
                .created(ldt)
                .build();
    }

    private OutItemDto createOutItemDto(ItemDto inputItemDto) {
        return OutItemDto.builder()
                .id(inputItemDto.getId())
                .name(inputItemDto.getName())
                .description(inputItemDto.getDescription())
                .available(inputItemDto.getAvailable())
                .request(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .build();
        }
}