package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper mapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(mapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User savedUser = userRepository.save(mapper.toUser(userDto));
        return mapper.toUserDto(savedUser);
    }

    @Override
    public UserDto getById(long id) {
        if (userRepository.findById(id).isEmpty())
            throw new UserNotFoundException("Not found user");
        return mapper.toUserDto(userRepository.getById(id));
    }

    @Override
    public UserDto patch(UserDto userDto) {
        return mapper.toUserDto(
                userRepository.save(
                        mapper.patchUserDtoToUser(userDto, userRepository.getById(userDto.getId()))));
    }

    @Override
    public void delete(long id) {
        if (!userRepository.existsById(id))
            throw new UserNotFoundException("User not found");
        userRepository.deleteById(id);
    }
}
