package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    @Autowired
    private final MockMvc mvc;

    @Autowired
    private final ObjectMapper mapper;

    @MockBean
    private final ItemService itemService;
    ItemDtoBooking itemDtoBooking = new ItemDtoBooking(1L,
            "name",
            "desc1",
            true,
            null,
            null,
            new HashSet<>(),
            null);

    ItemDto itemDto = new ItemDto("nameDto",
            "descDto",
            true,
            2L,
            1L,
            null);

    @SneakyThrows
    @Test
    void getAllItems() {
        when(itemService.getAllItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemDtoBooking));

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].available").value("true"))
                .andExpect(jsonPath("$[0].description").value("desc1"));

        verify(itemService).getAllItems(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void searchItem_whenTextValid_thenReturnedStatusOk() {

        when(itemService.search(any(), any(), any()))
                .thenReturn(Collections.singletonList(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "Dto")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("nameDto"))
                .andExpect(jsonPath("$[0].available").value("true"))
                .andExpect(jsonPath("$[0].description").value("descDto"));

        verify(itemService).search(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemDtoBooking);

        mvc.perform(get("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.description").value("desc1"));

        verify(itemService).getItem(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void addItem_whenItemValid_thenStatusOk() {
        when(itemService.addNewItem(any(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("nameDto"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.description").value("descDto"));

        verify(itemService).addNewItem(any(), any());
    }

    @SneakyThrows
    @Test
    void addItem_whenItemNonValid_thenStatusBadRequest() {
        itemDto.setDescription(null);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addNewItem(any(), any());
    }

    @SneakyThrows
    @Test
    void patchItem_whenItemValid_thenStatusOk() {
        ItemDto itemDtoUpdated = itemDto;
        when(itemService.patch(anyLong(), any(), anyLong()))
                .thenReturn(itemDtoUpdated);

        itemDtoUpdated.setName("updatedName");
        itemDtoUpdated.setId(2L);
        itemDtoUpdated.setAvailable(false);


        mvc.perform(patch("/items/{itemId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.available").value("false"))
                .andExpect(jsonPath("$.description").value("descDto"));

        verify(itemService).patch(anyLong(), any(), anyLong());
    }


    @SneakyThrows
    @Test
    void deleteItem() {

        mvc.perform(delete("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void createComment_whenCommentValid_thenStatusOk() {
        CommentDto commentDto = new CommentDto(1L, "comment", "user", LocalDateTime.now());

        when(itemService.createComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("comment"))
                .andExpect(jsonPath("$.authorName").value("user"));

        verify(itemService).createComment(any(), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void createComment_whenCommentNonValid_thenStatusBadRequest() {
        CommentDto wrongCommentDto = new CommentDto(1L, null, "user", LocalDateTime.now());

        mvc.perform(post("/items/{itemId}/comment", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(wrongCommentDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createComment(wrongCommentDto, 1L, 2L);

    }
}