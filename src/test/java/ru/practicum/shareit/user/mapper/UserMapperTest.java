package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;


class UserMapperTest {
    private UserMapper userMapper = new UserMapper();

    @Test
    void toUserDtoTest() {
        User user = new User(1L, "testName", "test@mail.com");
        UserDto expectedUser = new UserDto(1L, "testName", "test@mail.com");
        UserDto actualUser = userMapper.toUserDto(user);

        assertEquals(actualUser.toString(), expectedUser.toString());

    }

    @Test
    void toUser() {
        UserDto userDto = new UserDto(1L, "testName", "test@mail.com");
        User expectedUser = new User(1L, "testName", "test@mail.com");
        User actualUser = userMapper.toUser(userDto);

        assertEquals(actualUser.toString(), expectedUser.toString());
    }

    @Test
    void patchUserDtoToUser_whenDtoFieldsAreValid() {
        UserDto userDtoFromCont = new UserDto(1L, "NewTestName", "newEmail@mail.com");
        User oldUser = new User(2L, "testName", "test@mail.com");

        User expectedUser = new User(2L, "NewTestName", "newEmail@mail.com");
        User actualUser = userMapper.patchUserDtoToUser(userDtoFromCont, oldUser);

        assertEquals(actualUser.toString(), expectedUser.toString());

    }

    @Test
    void patchUserDtoToUser_whenDtoEmailIsNotValid() {
        UserDto userDtoFromCont = new UserDto(1L, "NewTestName", "newEmail@mail.com");
        userDtoFromCont.setEmail(null);
        User oldUser = new User(2L, "testName", "test@mail.com");

        User expectedUser = new User(2L, "NewTestName", "test@mail.com");
        User actualUser = userMapper.patchUserDtoToUser(userDtoFromCont, oldUser);

        assertEquals(actualUser.toString(), expectedUser.toString());

    }

    @Test
    void patchUserDtoToUser_whenDtoNameIsNotValid() {
        UserDto userDtoFromCont = new UserDto(1L, "NewTestName", "newEmail@mail.com");
        userDtoFromCont.setName(null);
        User oldUser = new User(2L, "testName", "test@mail.com");

        User expectedUser = new User(2L, "testName", "newEmail@mail.com");
        User actualUser = userMapper.patchUserDtoToUser(userDtoFromCont, oldUser);

        assertEquals(actualUser.toString(), expectedUser.toString());

    }
}