package ru.practicum.shareit.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(ItemRepository.class);


    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        List<Item> items = itemRepository.findItemsByUserId(userId);
        return items.stream().map(item -> mapper.toDto(item, userId, item.getId())).collect(Collectors.toList());

    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        checkUserExist(userId);
        Item savedItem = itemRepository.save(mapper.toItem(itemDto, userRepository.getById(userId)));
        return mapper.toDto(savedItem, userId, savedItem.getId());
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        validateAccess(userId, itemId);
        itemRepository.deleteItem(itemId);
    }

    @Override
    public ItemDto patch(Long userId, ItemDto itemDto, long itemId) {
        itemDto.setId(itemId);
        validateAccess(userId, itemId);
        Item savedItem = mapper.patchItemDtoToItem(itemRepository.findItem(itemId), itemDto);
        return mapper.toDto(itemRepository.patch(savedItem), userId, itemId);
    }

    @Override
    public ItemDto getItem(long itemId) {
        return mapper.toDto(itemRepository.findItem(itemId), itemRepository.findItem(itemId).getOwner().getId(), itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) return new ArrayList<>();
        List<Item> list = itemRepository.search(text);
        return list.stream().map(item -> mapper.toDto(item, item.getOwner().getId(), item.getId())).collect(Collectors.toList());

    }


    private void validateAccess(long userId, long itemId) {
        if (itemRepository.findItem(itemId).getOwner().getId() != userId) {
            throw new UserNotFoundException("Ошибка доступа!");
        }
    }

    private void checkUserExist(long userId) {
        try {
            userRepository.getById(userId);
        } catch (UserNotFoundException ex) {
            log.info("Не найден пользователь с id {}", userId);
        }
    }
}
