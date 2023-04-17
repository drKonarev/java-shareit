package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;
import ru.practicum.shareit.user.dto.UserDto;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;


    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        // public List<UserDto> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Get user with id = {}", id);
        return userClient.getById(id);
    }

    @PostMapping
    public ResponseEntity<Object> post(@Validated({Create.class}) @RequestBody UserDto user) {
        log.info("Create user {}", user);
        return userClient.saveUser(user);
    }

    @PatchMapping(path = "/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") long userId,
                                         @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Patch user with id={}, to {}", userId, userDto);
        userDto.setId(userId);
        return userClient.patch(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        return userClient.delete(userId);
    }
}
