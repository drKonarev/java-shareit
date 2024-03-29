package ru.practicum.shareit.item.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;

@Component
public class ItemMapper {

    public Item toItem(ItemDto dto, User user, ItemRequest itemRequest) {

        return new Item(dto.getId(),
                user,
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                itemRequest);
    }

    public ItemDto toDto(Item item, long userId, long dtoId) {
        if (item.getRequest() == null) {
            return new ItemDto(item.getName(),
                    item.getDescription(),
                    item.isAvailable(),
                    dtoId,
                    userId,
                    null);
        }
        return new ItemDto(item.getName(),
                item.getDescription(),
                item.isAvailable(),
                dtoId,
                userId,
                item.getRequest().getId());
    }

    public Item patchItemDtoToItem(Item item, ItemDto dto) {

        Item newItem = new Item();

        if (dto.getName() != null) {
            newItem.setName(dto.getName());
        } else newItem.setName(item.getName());


        if (dto.getDescription() != null) {
            newItem.setDescription(dto.getDescription());
        } else newItem.setDescription(item.getDescription());

        if (dto.getAvailable() != null) {
            newItem.setAvailable(dto.getAvailable());
        } else newItem.setAvailable(item.isAvailable());

        newItem.setId(item.getId());
        newItem.setOwner(item.getOwner());
        return newItem;
    }

    public ItemDtoBooking toDtoBooking(Item item) {
        if (item.getRequest() == null) {
            return new ItemDtoBooking(item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.isAvailable(),
                    null,
                    null,
                    new HashSet<>(),
                    null);
        }
        return new ItemDtoBooking(item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                null,
                null,
                new HashSet<>(),
                item.getRequest().getId());
    }

    public ItemDtoRequest toDtoRequest(Item item) {
        return new ItemDtoRequest(item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest().getId());
    }
}
