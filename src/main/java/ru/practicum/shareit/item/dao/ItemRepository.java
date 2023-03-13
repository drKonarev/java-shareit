package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> findItemsByUserId(long userId);

    Item save(Item item);

    void deleteItem(long itemId);

    Item patch(Item item);

    Item findItem(long itemId);

    List<Item> search(String string);


}
