package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItems(long userId);

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    void deleteItem(long userId, long itemId);

    ItemDto patch(Long userId, ItemDto itemDto, long itemId);

    ItemDto getItem(long userId, long itemId);

    List<ItemDto> search(String text);


}
