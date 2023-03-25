package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final ItemMapper itemMapper;

    private final ItemRepository itemRepository;

    public ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto, User owner) {
        return new ItemRequest(0L, itemRequestDto.getDescription(), owner, LocalDateTime.now());
    }

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        List<ItemDtoRequest> items = itemRepository.findAllByRequest_Id(itemRequest.getId())
                .stream()
                .map(itemMapper::toDtoRequest)
                .collect(Collectors.toList());
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items);

    }

}
