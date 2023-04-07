package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto getItemRequestById(Long itemRequestId, Long ownerId);

    ItemRequestDto save(ItemRequestDto itemRequestDto, Long ownerId);

    List<ItemRequestDto> getAllItemRequestsByOwnerId(Long ownerId);

    List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size);


}
