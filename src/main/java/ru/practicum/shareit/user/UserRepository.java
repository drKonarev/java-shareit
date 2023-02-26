package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User getById(long id);

    User save(User user);

    List<User> findAll();

    User update(User user);

    void delete(long id);

}
