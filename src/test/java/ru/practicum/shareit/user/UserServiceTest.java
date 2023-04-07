package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    User user = new User(1L, "userName", "user@mail.com");

    UserDto userDto = new UserDto(1L, "userName", "user@mail.com");

    @Test
    void getAllUsers_whenRepositoryIsEmpty_thenReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> actualList = userService.getAllUsers();

        assertTrue(actualList.isEmpty());
        verify(userRepository).findAll();
        verify(userMapper, never()).toUserDto(any());
    }

    @Test
    void getAllUsers_whenRepositoryIsNotEmpty_thenReturnSingletonList() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userMapper.toUserDto(any())).thenReturn(userDto);

        List<UserDto> actualList = userService.getAllUsers();

        assertFalse(actualList.isEmpty());
        assertEquals(userDto, actualList.get(0));
        verify(userRepository).findAll();
        verify(userMapper).toUserDto(any());
    }

    @Test
    void saveUser_whenUserIsValid_thenReturnedUser() {
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUser(any())).thenReturn(user);
        when(userMapper.toUserDto(any())).thenReturn(userDto);

        UserDto actualUser = userService.saveUser(userDto);

        assertEquals(actualUser, userDto);
        verify(userRepository).save(any());
        verify(userMapper).toUserDto(any());
        verify(userMapper).toUser(any());

    }


    @Test
    void getById_whenUserFound_thenReturnedUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(any())).thenReturn(userDto);

        UserDto actualUser = userService.getById(1L);

        assertEquals(actualUser, userDto);
        verify(userRepository).findById(anyLong());
        verify(userRepository).getById(anyLong());
        verify(userMapper).toUserDto(any());

    }


    @Test
    void getById_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void patch_whenUserIsExist_thenReturnedUpdatedUser() {
        when(userRepository.getById(anyLong())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserDto(any())).thenReturn(userDto);
        when(userMapper.patchUserDtoToUser(any(), any())).thenReturn(user);

        UserDto actualUser = userService.patch(userDto);

        assertEquals(actualUser, userDto);
        verify(userRepository).save(eq(user));
        verify(userRepository).getById(eq(1L));
        verify(userMapper).toUserDto(eq(user));
        verify(userMapper).patchUserDtoToUser(eq(userDto), eq(user));

    }


    @Test
    void delete_whenRepositoryIsNotEmpty_thenRepositorySizeIsReduce() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        userService.delete(anyLong());
        verify(userRepository).deleteById(anyLong());
    }

    @Test
    void deleteNotExistUser_then() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.delete(anyLong()));
    }
}