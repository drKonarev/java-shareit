package ru.practicum.shareit.item.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.PostNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;


@Repository
public class ItemRepositoryImpl implements ItemRepository {


    HashMap<Long, List<Item>> storage = new HashMap<>(); // Long - id user,
    private static final Logger log = LoggerFactory.getLogger(ItemRepository.class);


    @Override
    public List<Item> findItemsByUserId(long userId) {
        log.info("Найдены записи пользователя с айДи: {}", userId);
        return storage.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item save(Item item) {
        item.setId(getId());
        if (!storage.containsKey(item.getOwner().getId())) {
            throw new UserNotFoundException("Не найден пользователь!");
        }
        List<Item> list = new ArrayList<>(storage.get(item.getOwner().getId()));
        list.add(item);
        storage.put(item.getOwner().getId(), list);
        log.info("Создана запись под айди: {}", item.getId());
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        if (storage.containsKey(userId)) {
            storage.get(userId).removeIf(item -> item.getId() == (itemId));
            log.info("Удалена запись {} пользователя {}", itemId, userId);
        }

    }

    @Override
    public Item patch(Item item) {
        if (storage.containsKey(item.getOwner().getId())) {
            storage.get(item.getOwner().getId()).removeIf(item1 -> item1.getId() == item.getId());
            save(item);

        } else {
            throw new UserNotFoundException("Пользователь не найден!");
        }
        return item;
    }

    @Override
    public Item findItem(long userId, long itemId) {
        if (storage.containsKey(userId)) {
            if (storage.get(userId).stream().anyMatch(item -> item.getId() == itemId))
                return storage.get(userId).stream().filter(item -> item.getId() == itemId).findAny().get();
            else throw new PostNotFoundException("Пост не найден!");
        }
        throw new UserNotFoundException("Не найден пользователь!");
    }


    @Override
    public void addUser(long userId) {
        storage.put(userId, Collections.emptyList());
        log.info("Добавлен пользователь под айДи: " + userId);
    }

    @Override
    public Item getItem(long itemId) {
        if (storage.values().stream().flatMap(Collection::stream).anyMatch(item -> item.getId() == itemId)) {
            return storage.values().stream().flatMap(Collection::stream).filter(item -> item.getId() == itemId).findAny().get();
        } else throw new PostNotFoundException("Пост не найден!");

    }

    @Override
    public List<Item> search(String string) {
        List<Item> items = storage.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        log.info("Отправлен запрос на поиск по ключевому слову: " + string);
        return items.stream().filter(item -> (item.getDescription() + item.getName()).toLowerCase().contains(string) && item.isAvailable()).collect(Collectors.toList());

    }

    @Override
    public void deleteUser(long userId) {
        storage.remove((Long) userId);
        log.info("Из хранилища постов удален пользователь под АйДи: " + userId);
    }

    private long getId() {
        long lastId = storage.values().stream().flatMap(Collection::stream).mapToLong(Item::getId).max().orElse(0);
        return lastId + 1;
    }
}
