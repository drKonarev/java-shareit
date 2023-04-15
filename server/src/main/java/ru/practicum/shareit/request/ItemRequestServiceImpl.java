package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestMapper itemRequestMapper;

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;


    @Override
    public ItemRequestDto getItemRequestById(Long itemRequestId, Long userId) {
        if (userRepository.findById(userId).isEmpty()) throw new UserNotFoundException("Пользователь не найден!");
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemRequestId);
        if (itemRequest.isEmpty()) throw new ItemNotFoundException("Такого запроса не существует!");
        return itemRequestMapper.toDto(itemRequest.get());
    }

    @Override
    public ItemRequestDto save(ItemRequestDto itemRequestDto, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) throw new UserNotFoundException("Пользователь не найден!");
        ItemRequest request = itemRequestRepository.save(itemRequestMapper.dtoToItemRequest(itemRequestDto, user.get()));
        return itemRequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsByOwnerId(Long ownerId) {
        if (userRepository.findById(ownerId).isEmpty()) throw new UserNotFoundException("Пользователь не найден!");
        return itemRequestRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(itemRequestMapper::toDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        return itemRequestRepository.findAllExtendUserId(userId, PageRequest.of(from, size))
                .stream()
                .map(itemRequestMapper::toDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }
}
