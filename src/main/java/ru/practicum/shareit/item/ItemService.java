package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.List;

public interface ItemService {

    List<ItemDtoBooking> getAllItems(long userId, Integer from, Integer size);

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    void deleteItem(long userId, long itemId);

    ItemDto patch(Long userId, ItemDto itemDto, long itemId);

    ItemDtoBooking getItem(long itemId, long userId);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto createComment(CommentDto comment, long userId, long itemId);

}
