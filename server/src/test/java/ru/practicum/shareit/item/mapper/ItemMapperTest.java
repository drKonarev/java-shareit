package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {
    private final ItemMapper itemMapper = new ItemMapper();
    User user = new User(1L, "name", "mail@mail.com");
    Item item = new Item(1L, user, "name", "desc", true, null);

    ItemDto itemDto = new ItemDto("name", "desc", true, 1L, 1L, null);
    BookingItemDto booking1 = new BookingItemDto(1L, 1L);
    ItemDtoBooking itemDtoBooking = new ItemDtoBooking(1L, "name", "desc", true, booking1,
            null, Collections.emptySet(), null);
    ItemDtoRequest itemDtoRequest = new ItemDtoRequest(1L, "name", "desc", true, null);

    ItemRequest itemRequest = new ItemRequest(1L, "request", user, LocalDateTime.now());

    @Test
    void toItem() {
        Item expectedItem = new Item(1L, user, "name", "desc", true, itemRequest);

        Item actualItem = itemMapper.toItem(itemDto, user, itemRequest);

        assertEquals(expectedItem.toString(), actualItem.toString());
    }

    @Test
    void toDtowhenRequestIsNull() {
        ItemDto expectedItemDto = new ItemDto("name", "desc", true, 1L, 1L, null);
        ItemDto actualItemDto = itemMapper.toDto(item, 1L, 1L);

        assertEquals(expectedItemDto.toString(), actualItemDto.toString());
    }

    @Test
    void toDto_whenRequestIsNotNull() {
        item.setRequest(itemRequest);
        ItemDto expectedItemDto = new ItemDto("name", "desc", true, 1L, 1L, 1L);
        ItemDto actualItemDto = itemMapper.toDto(item, 1L, 1L);

        assertEquals(expectedItemDto.toString(), actualItemDto.toString());
    }

    @Test
    void patchItemDtoToItem_whenAllFieldsAreValid() {
        Item expectedItem = item;
        expectedItem.setAvailable(false);
        expectedItem.setDescription("newDesc");

        ItemDto itemDtoForPatch = new ItemDto("name", "newDesc", false, 1L, 1L, null);
        Item actualItem = itemMapper.patchItemDtoToItem(item, itemDtoForPatch);

        assertEquals(expectedItem.toString(), actualItem.toString());
    }

    @Test
    void patchItemDtoToItem_whenNameIsntValid() {
        Item expectedItem = item;
        expectedItem.setAvailable(false);
        expectedItem.setDescription("newDesc");

        ItemDto itemDtoForPatch = new ItemDto(null, "newDesc", false, 1L, 1L, null);
        Item actualItem = itemMapper.patchItemDtoToItem(item, itemDtoForPatch);

        assertEquals(expectedItem.toString(), actualItem.toString());
    }

    @Test
    void patchItemDtoToItem_whenAvailableIsntValid() {
        Item expectedItem = item;
        expectedItem.setDescription("newDesc");

        ItemDto itemDtoForPatch = new ItemDto(null, "newDesc", null, 1L, 1L, null);
        Item actualItem = itemMapper.patchItemDtoToItem(item, itemDtoForPatch);

        assertEquals(expectedItem.toString(), actualItem.toString());
    }

    @Test
    void patchItemDtoToItem_whenDescriptionIsntValid() {
        Item expectedItem = item;
        expectedItem.setAvailable(false);

        ItemDto itemDtoForPatch = new ItemDto("name", null, false, 1L, 1L, null);
        Item actualItem = itemMapper.patchItemDtoToItem(item, itemDtoForPatch);

        assertEquals(expectedItem.toString(), actualItem.toString());
    }

    @Test
    void toDtoBooking_whenRequestNotExist() {
        ItemDtoBooking expected = itemDtoBooking;
        expected.setLastBooking(null);

        ItemDtoBooking actual = itemMapper.toDtoBooking(item);

        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void toDtoBooking_whenRequestExist() {
        ItemDtoBooking expected = itemDtoBooking;
        expected.setLastBooking(null);
        expected.setRequestItemId(1L);

        item.setRequest(itemRequest);

        ItemDtoBooking actual = itemMapper.toDtoBooking(item);

        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void toDtoRequest() {
        item.setRequest(itemRequest);
        itemDtoRequest.setRequestId(1L);

        ItemDtoRequest actual = itemMapper.toDtoRequest(item);

        assertEquals(itemDtoRequest.toString(), actual.toString());
    }
}