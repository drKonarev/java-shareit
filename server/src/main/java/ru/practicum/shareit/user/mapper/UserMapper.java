package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }


    public User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public User patchUserDtoToUser(UserDto userDto, User user) {
        User newUser = new User();

        if (userDto.getEmail() != null) {
            newUser.setEmail(userDto.getEmail());
        } else {
            newUser.setEmail(user.getEmail());
        }

        if (userDto.getName() != null) {
            newUser.setName(userDto.getName());
        } else {
            newUser.setName(user.getName());
        }

        newUser.setId(user.getId());

        return newUser;
    }
}
