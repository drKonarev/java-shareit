package ru.practicum.shareit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.UserAlreadyExistException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Repository
public class UserRepositoryImpl implements UserRepository {
    HashMap<Long, User> storage = new HashMap<>();
    Long id = 0L;
    private static final Logger log = LoggerFactory.getLogger(ItemRepository.class);

    @Override
    public User getById(long id) {
        if (!storage.containsKey(id))
            throw new UserNotFoundException("Пользователь с таким айди не найден!");
        log.info("Запрос пользователя: " + storage.get(id).getName());
        return storage.get(id);
    }

    @Override
    public User save(User user) {
        checkUnicEmail(user);
        user.setId(getId());
        storage.put(user.getId(), user);
        log.info("Создан пользователь: " + user.getName());
        return user;
    }

    @Override
    public List<User> findAll() {
        log.info("Запрос на всех пользователей");
        return new ArrayList<>(storage.values());
    }

    @Override
    public User update(User user) {
        if (!storage.containsKey(user.getId()))
            throw new UserNotFoundException("Пользователь с таким айди не найден!");
        checkUnicEmail(user);
        storage.remove(user.getId());
        storage.put(user.getId(), user);
        log.info("Обновлен пользователь: " + user.getName());

        return user;
    }

    @Override
    public void delete(long id) {
        storage.remove((Long) id);
        log.info("Удален пользователь под айДи: " + id);
    }

    private void checkUnicEmail(User user) {
        if (storage.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()) && user1.getId() != user.getId()))
            throw new UserAlreadyExistException("Пользователь с таким мылом уже есть!");
    }

    private long getId() {
        return ++id;
    }
}
