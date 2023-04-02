package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private CommentMapper commentMapper;

    User user = new User(1L, "user", "email@email.com");
    Item item = new Item(1L, user, "item", "desc", true, null);
    ItemDto itemDto = new ItemDto("item", "desc", true, 1L, 1L, null);
    ItemDtoRequest itemDtoRequest = new ItemDtoRequest(1L, "item", "desc", true, null);
    ItemDtoBooking itemDtoBooking = new ItemDtoBooking(1L, "item", "desc", true, null, null, Collections.emptySet(), null);
    BookingDto bookingDto = new BookingDto(1L, 1L, LocalDateTime.now(),
            LocalDateTime.now().plusHours(6), BookingStatus.WAITING, 1L);

    Booking booking = new Booking(1L, item, user, LocalDateTime.now(),
            LocalDateTime.now().plusHours(6), BookingStatus.WAITING);

    BookingItemDto bookingItemDto = new BookingItemDto(1L, 1L);

    BookingResponseDto bookingResponseDto = new BookingResponseDto(1L, LocalDateTime.now(),
            LocalDateTime.now().plusHours(6), BookingStatus.WAITING, user, item);

    Comment comment = new Comment(1L, "text", user, item, LocalDateTime.now());

    CommentDto commentDto = new CommentDto(1L, "text", "user", LocalDateTime.now());
    ItemRequest itemRequest = new ItemRequest(1L, "desc", user, LocalDateTime.now());


    @Test
    void addNewItem_whenUserExistAndItemRequestNotNull_thenOk() {
        itemDto.setRequestId(1L);
        item.setRequest(itemRequest);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.getById(1L)).thenReturn(user);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.toItem(any(), any(), any())).thenReturn(item);
        when(itemMapper.toDto(any(), anyLong(), anyLong())).thenReturn(itemDto);

        ItemDto actual = itemService.addNewItem(1L, itemDto);

        assertEquals(itemDto.toString(), actual.toString());
        verify(itemRepository).save(any());

    }

    @Test
    void addNewItem_whenUserExistAndItemRequestNull_thenOk() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.getById(1L)).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.toItem(any(), any(), any())).thenReturn(item);
        when(itemMapper.toDto(any(), anyLong(), anyLong())).thenReturn(itemDto);

        ItemDto actual = itemService.addNewItem(1L, itemDto);

        assertEquals(itemDto.toString(), actual.toString());
        verify(itemRepository).save(any());

    }

    @Test
    void addNewItem_whenUserNotFound_thenThrow() {  // когда нет юзера
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.addNewItem(1L, new ItemDto()));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void deleteItem_byOwner_thenValidateIsOk() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        itemService.deleteItem(1L, 1L);
        verify(itemRepository).deleteById(1L);
    }

    @Test
    void deleteItem_byOtherUser_thenThrowUserNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        assertThrows(UserNotFoundException.class, () -> itemService.deleteItem(2L, 1L));
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void patch_byOwner_thenValidateIsOk() {
        ItemDto itemDtoToPatch = new ItemDto("itemUpdated", "descUpdated", false, 1L, 1L, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Item pathcedItem = new Item(1L, user, "itemUpdated", "descUpdated", false, null);
        when(itemMapper.patchItemDtoToItem(item, itemDtoToPatch)).thenReturn(pathcedItem);
        when(itemMapper.toDto(any(), anyLong(), anyLong())).thenReturn(itemDtoToPatch);

        ItemDto actual = itemService.patch(1L, itemDtoToPatch, 1L);

        assertEquals(itemDtoToPatch.toString(), actual.toString());
        verify(itemRepository).save(any());
        verify(itemRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void patch_byOtherUser_thenThrowUserNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(UserNotFoundException.class, () -> itemService.patch(2L, itemDto, 1L));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItem_whenItemNotFound_thenThrow() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItem(1L, 1L));
        verify(bookingRepository, never()).getLastBookingByItemId(anyLong());
        verify(bookingRepository, never()).getNextBookingByItemId(anyLong());
        verify(commentRepository, never()).findAllByItem_Id(anyLong());
    }

    @Test
    void getItemByOwner_whenItemFoundAndNextBookingPresent() {
        itemDtoBooking.setNextBooking(bookingItemDto);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toDtoBooking(item)).thenReturn(itemDtoBooking);
        when(bookingRepository.getNextBookingByItemId(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.bookingToItem(booking)).thenReturn(bookingItemDto);
        when(bookingRepository.getLastBookingByItemId(anyLong())).thenReturn(Optional.empty());
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(Collections.singletonList(comment));
        when(commentMapper.toDto(any())).thenReturn(commentDto);

        ItemDtoBooking actual = itemService.getItem(1L, 1L);
        assertEquals(itemDtoBooking.toString(), actual.toString());

    }

    @Test
    void getItemByOwner_whenItemFoundAndLastBookingPresent() {
        itemDtoBooking.setLastBooking(bookingItemDto);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toDtoBooking(item)).thenReturn(itemDtoBooking);
        when(bookingRepository.getLastBookingByItemId(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.bookingToItem(booking)).thenReturn(bookingItemDto);
        when(bookingRepository.getNextBookingByItemId(anyLong())).thenReturn(Optional.empty());
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(Collections.singletonList(comment));
        when(commentMapper.toDto(any())).thenReturn(commentDto);

        ItemDtoBooking actual = itemService.getItem(1L, 1L);
        assertEquals(itemDtoBooking.toString(), actual.toString());
    }

    @Test
    void getItemByOtherUser_whenItemFound() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toDtoBooking(item)).thenReturn(itemDtoBooking);
        when(bookingRepository.getLastBookingByItemId(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.bookingToItem(booking)).thenReturn(bookingItemDto);
        when(bookingRepository.getNextBookingByItemId(anyLong())).thenReturn(Optional.empty());
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(Collections.singletonList(comment));
        when(commentMapper.toDto(any())).thenReturn(commentDto);

        ItemDtoBooking actual = itemService.getItem(1L, 2L);
        assertEquals(itemDtoBooking.toString(), actual.toString());
    }

    @Test
    void search_whenTextIsBlank_thenReturnedEmptyList() {
        List<ItemDto> actual = itemService.search("", 0, 10);
        assertEquals(Collections.emptyList(), actual);
        verify(itemRepository, never()).searchByDescriptionAndName(anyString(), any());
    }

    @Test
    void search_whenTextIsNotBlank_thenReturnedList() {
        when(itemRepository.searchByDescriptionAndName(anyString(), any()))
                .thenReturn(new PageImpl<Item>(Collections.singletonList(item)));
        when(itemMapper.toDto(any(), anyLong(), anyLong())).thenReturn(itemDto);

        List<ItemDto> actual = itemService.search("safd", 0, 10);

        assertFalse(actual.isEmpty());
        assertEquals(itemDto.toString(), actual.get(0).toString());
    }

    @Test
    void createComment_whenUserNotFound_thenThrow() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.createComment(commentDto, 1L, 1L));
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).findPastBookingByBooker_IdAndItem_Id(anyLong(), anyLong());
        verify(commentRepository, never()).save(any());


    }

    @Test
    void createComment_whenItemNotFound_thenThrow() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.createComment(commentDto, 1L, 1L));
        verify(bookingRepository, never()).findPastBookingByBooker_IdAndItem_Id(anyLong(), anyLong());
        verify(commentRepository, never()).save(any());

    }

    @Test
    void createComment_whenPastBookingNotFound_thenThrowNullPointerException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findPastBookingByBooker_IdAndItem_Id(anyLong(), anyLong())).thenReturn(Collections.emptyList());

        assertThrows(NullPointerException.class, () -> itemService.createComment(commentDto, 1L, 1L));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenEverythingIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findPastBookingByBooker_IdAndItem_Id(anyLong(), anyLong())).thenReturn(Collections.singletonList(booking));
        when(commentMapper.dtoToComment(any(), any(), any())).thenReturn(comment);
        when(commentRepository.save(any())).thenReturn(comment);
        when(commentMapper.toDto(any())).thenReturn(commentDto);

        CommentDto actual = itemService.createComment(commentDto, 1L, 1L);
        assertEquals(commentDto, actual);
    }


}