package ru.practicum.shareit.item.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.PostNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;


@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private static final Logger log = LoggerFactory.getLogger(ItemRepository.class);


    HashMap<Long, Item> storage = new HashMap<>(); // Long - id item,


    @Override
    public List<Item> findItemsByUserId(long userId) {
        log.info("Найдены записи пользователя с айДи: {}", userId);
        return storage.values()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        item.setId(getId());
        storage.put(item.getId(), item);
        log.info("Создана запись под айди: {}", item.getId());
        return item;
    }

    @Override
    public void deleteItem(long itemId) {
        storage.remove(itemId);
        log.info("Удалена запись {}", itemId);
    }

    @Override
    public Item patch(Item item) {
        storage.remove(item.getId());
        storage.put(item.getId(), item);
        log.info("Обновлена вещь : {}", item.getName());
        return item;
    }

    @Override
    public Item findItem(long itemId) {
        if (!storage.containsKey(itemId))
            throw new PostNotFoundException("Пост не найден!");
        return storage.get(itemId);
    }


    @Override
    public List<Item> search(String string) {
        log.info("Отправлен запрос на поиск по ключевому слову: " + string);
        return storage.values()
                .stream()
                .filter(item -> (item.getDescription() + item.getName()).toLowerCase().contains(string)
                        && item.isAvailable())
                .collect(Collectors.toList());

    }


    private long getId() {
        return storage.size() + 1;
    }
}
