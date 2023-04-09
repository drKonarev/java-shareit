package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemRequestMapperTest {
    User owner = new User(1L, "name", "mail@mail.com");
    ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "desc", LocalDateTime.now(), Collections.emptyList());
    ItemRequest itemRequest = new ItemRequest(1L, "desc", owner, LocalDateTime.now());

    @Autowired
    ItemRequestMapper itemRequestMapper;

    @Test
    void dtoToItemRequest() {
        ItemRequest actualItemRequest = itemRequestMapper.dtoToItemRequest(itemRequestDto, owner);

        assertEquals(0L, actualItemRequest.getId());
        assertEquals("desc", actualItemRequest.getDescription());
        assertEquals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")), actualItemRequest.getCreated().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")));


    }

    @Test
    void toDto() {
        ItemRequestDto actualItemRequest = itemRequestMapper.toDto(itemRequest);

        assertEquals(1L, actualItemRequest.getId());
        assertEquals("desc", actualItemRequest.getDescription());
        assertEquals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm")), actualItemRequest.getCreated().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm")));

    }
}