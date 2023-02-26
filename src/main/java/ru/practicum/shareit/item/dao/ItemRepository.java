package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> findItemsByUserId(long userId);

    Item save(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);

    Item patch(Item item);

    Item findItem(long userId, long itemId);

    void addUser(long userId);

    Item getItem(long itemId);

    List<Item> search(String string);

    void deleteUser(long userId);

}
