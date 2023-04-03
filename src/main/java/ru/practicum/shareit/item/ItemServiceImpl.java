package ru.practicum.shareit.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentMapper commentMapper;
    private static final Logger log = LoggerFactory.getLogger(ItemRepository.class);


    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           BookingMapper bookingMapper,
                           ItemMapper itemMapper,
                           ItemRequestRepository itemRequestRepository, CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.bookingMapper = bookingMapper;
        this.itemMapper = itemMapper;
        this.itemRequestRepository = itemRequestRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public List<ItemDtoBooking> getAllItems(long userId, Integer from, Integer size) {
        List<Item> items = itemRepository.findItemByOwnerId(userId, PageRequest.of(from, size)).toList();
        return items.stream().sorted(Comparator.comparing(Item::getId)).map(this::addBookingsAndComments).collect(Collectors.toList());

    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        checkUserExist(userId);
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null)
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).get();
        Item savedItem = itemRepository.save(itemMapper.toItem(itemDto, userRepository.getById(userId), itemRequest));
        return itemMapper.toDto(savedItem, userId, savedItem.getId());
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        validateAccess(userId, itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto patch(Long userId, ItemDto itemDto, long itemId) {
        itemDto.setId(itemId);
        validateAccess(userId, itemId);
        Item savedItem = itemMapper.patchItemDtoToItem(itemRepository.findById(itemId).get(), itemDto);
        return itemMapper.toDto(itemRepository.save(savedItem), userId, itemId);
    }

    @Override
    public ItemDtoBooking getItem(long itemId, long userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) throw new ItemNotFoundException("Item not found!");
        ItemDtoBooking booking = addBookingsAndComments(item.get());
        if (item.get().getOwner().getId() == userId)
            return booking;
        booking.setLastBooking(null);
        booking.setNextBooking(null);
        return booking;
    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) return new ArrayList<>();
        List<Item> list = itemRepository.searchByDescriptionAndName(text, PageRequest.of(from, size)).toList();
        return list.stream().map(item -> itemMapper.toDto(item, item.getOwner().getId(), item.getId())).collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto comment, long userId, long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User not found");
        });
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new ItemNotFoundException("Item not found");
        });
        if (bookingRepository.findPastBookingByBooker_IdAndItem_Id(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new NullPointerException("Not found such booking!");
        }
        Comment newComment = commentRepository.save(commentMapper.dtoToComment(comment, user, item));

        return commentMapper.toDto(newComment);
    }


    private void validateAccess(long userId, long itemId) {
        if (itemRepository.findById(itemId).get().getOwner().getId() != userId) {
            throw new UserNotFoundException("Ошибка доступа!");
        }
    }

    private void checkUserExist(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.info("Не найден пользователь с id {}", userId);
            throw new UserNotFoundException("Not found user!");
        }
    }

    private ItemDtoBooking addBookingsAndComments(Item item) {
        ItemDtoBooking itemBooking = itemMapper.toDtoBooking(item);
        if (bookingRepository.getNextBookingByItemId(item.getId()).isPresent()) {
            BookingItemDto nextBooking = bookingMapper.bookingToItem(bookingRepository.getNextBookingByItemId(item.getId()).get());
            itemBooking.setNextBooking(nextBooking);
        }
        if (bookingRepository.getLastBookingByItemId(item.getId()).isPresent()) {
            BookingItemDto lastBooking = bookingMapper.bookingToItem(bookingRepository.getLastBookingByItemId(item.getId()).get());
            itemBooking.setLastBooking(lastBooking);
        }
        itemBooking.setComments(commentRepository.findAllByItem_Id(item.getId())
                .stream().map(commentMapper::toDto)
                .collect(Collectors.toSet()));
        return itemBooking;
    }

}
