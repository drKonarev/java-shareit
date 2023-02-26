package ru.practicum.shareit.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.user.dto.UserDto;


import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserDto post(@Validated({Create.class}) @RequestBody UserDto user) {
        return userService.saveUser(user);
    }

    @PatchMapping(path = "/{userId}")
    public UserDto update(@PathVariable("userId") long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        return userService.patch(userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
