package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.error.AccessOrAvailableException;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class BookingServiceTest {

    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper mapper;

    User user = new User(1L, "name", "email@email.com");
    Item item = new Item(1L, user, "item", "desc", true, null);

    BookingDto bookingDto = new BookingDto(1L, 1L, LocalDateTime.now(),
            LocalDateTime.now().plusHours(6), BookingStatus.WAITING, 1L);

    Booking booking = new Booking(1L, item, user, LocalDateTime.now(),
            LocalDateTime.now().plusHours(6), BookingStatus.WAITING);

    BookingResponseDto bookingResponseDto = new BookingResponseDto(1L, LocalDateTime.now(),
            LocalDateTime.now().plusHours(6), BookingStatus.WAITING, user, item);


    @Test
    void save_whenStartTimeAfterEndTime_thenThrowNullPointer() {
        bookingDto.setEnd(LocalDateTime.now().minusDays(2));

        assertThrows(NullPointerException.class, () -> bookingService.save(bookingDto, 1L));
        verify(itemRepository, never()).findById(any());
        verify(userRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void save_whenStartTimeEqualsEndTime_thenThrowNullPointer() {
        bookingDto.setEnd(bookingDto.getStart());

        assertThrows(NullPointerException.class, () -> bookingService.save(bookingDto, 1L));
        verify(itemRepository, never()).findById(any());
        verify(userRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void save_whenItemNotFound_thenThrowException() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> bookingService.save(bookingDto, 1L));
        verify(itemRepository).findById(any());
        verify(userRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void save_whenUserNotFound_thenThrowException() {
        when(itemRepository.findById(any())).thenReturn(Optional.of(new Item()));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.save(bookingDto, 1L));
        verify(itemRepository).findById(any());
        verify(userRepository).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void save_whenItemNotAvailable_thenThrowNullPointerException() {
        item.setAvailable(false);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        assertThrows(NullPointerException.class, () -> bookingService.save(bookingDto, 1L));
        verify(itemRepository).findById(any());
        verify(userRepository).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void save_whenHaveNoAccessToPublic_thenThrowAccessOrAvailableException() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));


        assertThrows(AccessOrAvailableException.class, () -> bookingService.save(bookingDto, 1L));
        verify(itemRepository).findById(any());
        verify(userRepository).findById(any());
        verify(bookingRepository, never()).save(any());

    }

    @Test
    void save_whenEverythingIsOk() {

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(mapper.dtoToBooking(any(), any(), any())).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actual = bookingService.save(bookingDto, 2L);

        assertEquals(bookingResponseDto.toString(), actual.toString());

        verify(itemRepository).findById(any());
        verify(userRepository).findById(any());
        verify(bookingRepository).save(any());

    }

    @Test
    void updateStatus_whenStatusIsNull_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateStatus(1L, null, 1L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateStatus_whenBookingNotFound_thenThrowItemNotFoundException() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.updateStatus(1L, true, 1L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateStatus_whenStatusAlreadyExist_thenThrowNullPointerException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        assertThrows(NullPointerException.class,
                () -> bookingService.updateStatus(1L, true, 1L));
        verify(bookingRepository, never()).save(any());

    }

    @Test
    void updateStatus_whenHaveNoAccessToProve_thenThrowAccessOrAvailableException() {

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        assertThrows(AccessOrAvailableException.class,
                () -> bookingService.updateStatus(1L, true, 2L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateStatus_whenEverythingIsOk_andStatusIsTrue() {
        Booking bookingApproved = new Booking(1L, null, null, LocalDateTime.now(),
                LocalDateTime.now().plusHours(6), BookingStatus.WAITING);

        BookingResponseDto bookingResponseDtoApproved = new BookingResponseDto(1L, LocalDateTime.now(),
                LocalDateTime.now().plusHours(6), BookingStatus.WAITING, null, null);

        bookingApproved.setBooker(user);
        bookingApproved.setItem(item);
        bookingResponseDtoApproved.setItem(item);
        bookingResponseDtoApproved.setBooker(user);

        bookingApproved.setStatus(BookingStatus.APPROVED);

        bookingResponseDtoApproved.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(bookingApproved);
        when(mapper.bookingToResponse(any())).thenReturn(bookingResponseDtoApproved);


        BookingResponseDto actual = bookingService.updateStatus(1L, true, 1L);
        assertEquals(BookingStatus.APPROVED, actual.getStatus());
        verify(bookingRepository).save(any());
    }

    @Test
    void updateStatus_whenEverythingIsOk_andStatusIsFalse() {
        Booking bookingRejected = new Booking(1L, null, null, LocalDateTime.now(),
                LocalDateTime.now().plusHours(6), BookingStatus.WAITING);

        BookingResponseDto bookingResponseDtoRejected = new BookingResponseDto(1L, LocalDateTime.now(),
                LocalDateTime.now().plusHours(6), BookingStatus.WAITING, null, null);

        bookingRejected.setBooker(user);
        bookingRejected.setItem(item);
        bookingResponseDtoRejected.setItem(item);
        bookingResponseDtoRejected.setBooker(user);
        bookingRejected.setStatus(BookingStatus.REJECTED);
        bookingResponseDtoRejected.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(bookingRejected);
        when(mapper.bookingToResponse(any())).thenReturn(bookingResponseDtoRejected);


        BookingResponseDto actual = bookingService.updateStatus(1L, false, 1L);
        assertEquals(BookingStatus.REJECTED, actual.getStatus());
        verify(bookingRepository).save(any());
    }

    @Test
    void getById_whenBookingNotFound_thenThrow() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getById(1L, 1L));
    }

    @Test
    void getById_whenUserNotFound_thenThrow() {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getById(1L, 1L));

    }

    @Test
    void getByID_whenHaveNoAccess_thenThrowAccessOrAvailableException() {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));


        assertThrows(AccessOrAvailableException.class,
                () -> bookingService.getById(1L, 2L));
        verify(mapper, never()).bookingToResponse(any());
    }

    @Test
    void getById_whenEverythingIsOk() {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actual = bookingService.getById(1L, 1L);

        assertEquals(bookingResponseDto.toString(), actual.toString());
        verify(bookingRepository).findById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getAllBookingByUserId_whenUserNotFound_thenThrow() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllBookingByUserId(1L, "ALL", 0, 10));
    }

    @Test
    void getAllBookingByUserId_whenStateALL() {
        booking.setStatus(BookingStatus.APPROVED);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);

        List<BookingResponseDto> actual = bookingService.getAllBookingByUserId(1L, "ALL", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByUserId_whenStateCURRENT() {
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusHours(1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);

        List<BookingResponseDto> actual = bookingService.getAllBookingByUserId(1L, "CURRENT", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByUserId_whenStatePAST() {
        booking.setStart(LocalDateTime.now().minusHours(6));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setStatus(BookingStatus.APPROVED);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);

        List<BookingResponseDto> actual = bookingService.getAllBookingByUserId(1L, "PAST", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByUserId_whenStateFUTURE() {
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.APPROVED);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);
        List<BookingResponseDto> actual = bookingService.getAllBookingByUserId(1L, "FUTURE", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByUserId_whenStateREJECTED() {
        booking.setStatus(BookingStatus.REJECTED);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);
        List<BookingResponseDto> actual = bookingService.getAllBookingByUserId(1L, "REJECTED", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByUserId_whenStateWAITING() {
        booking.setStatus(BookingStatus.WAITING);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);
        List<BookingResponseDto> actual = bookingService.getAllBookingByUserId(1L, "WAITING", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByUserId_whenStateUNKOWN() {
        booking.setStatus(null);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingByUserId(1L, "UKNOWN", 0, 10));
    }

    @Test
    void getAllBookingByOwnerId_whenUserNotFound_thenThrow() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllBookingByOwnerId(1L, "ALL", 0, 10));
    }

    @Test
    void getAllBookingByOwnerId_whenBookingsIsEmpty_thenReturnedEmptyList() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItem_Owner_Id(1L, PageRequest.of(0, 10)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> actual = bookingService.getAllBookingByOwnerId(1L, "ALL", 0, 10);

        assertTrue(actual.isEmpty());
    }

    @Test
    void getAllBookingByOwnerId_whenStateIsALL() {
        booking.setStatus(BookingStatus.APPROVED);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItem_Owner_Id(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);

        List<BookingResponseDto> actual = bookingService.getAllBookingByOwnerId(1L, "ALL", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByOwnerId_whenStateIsCURRENT() {
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusHours(1));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItem_Owner_Id(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);

        List<BookingResponseDto> actual = bookingService.getAllBookingByOwnerId(1L, "CURRENT", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByOwnerId_whenStateIsPAST() {
        booking.setStart(LocalDateTime.now().minusHours(6));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setStatus(BookingStatus.APPROVED);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItem_Owner_Id(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);

        List<BookingResponseDto> actual = bookingService.getAllBookingByOwnerId(1L, "PAST", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());

    }

    @Test
    void getAllBookingByOwnerId_whenStateIsFUTURE() {
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.APPROVED);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItem_Owner_Id(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);
        List<BookingResponseDto> actual = bookingService.getAllBookingByOwnerId(1L, "FUTURE", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByOwnerId_whenStateIsREJECTED() {
        booking.setStatus(BookingStatus.REJECTED);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItem_Owner_Id(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);
        List<BookingResponseDto> actual = bookingService.getAllBookingByOwnerId(1L, "REJECTED", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByOwnerId_whenStateIsWAITING() {
        booking.setStatus(BookingStatus.WAITING);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItem_Owner_Id(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        when(mapper.bookingToResponse(booking)).thenReturn(bookingResponseDto);
        List<BookingResponseDto> actual = bookingService.getAllBookingByOwnerId(1L, "WAITING", 0, 10);
        assertFalse(actual.isEmpty());
        assertEquals(bookingResponseDto.toString(), actual.get(0).toString());
    }

    @Test
    void getAllBookingByOwnerId_whenStateIsIllegal_thenThrowIllegalArgumentException() {
        booking.setStatus(null);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItem_Owner_Id(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<Booking>(Collections.singletonList(booking)));
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingByOwnerId(1L, "UKNOWN", 0, 10));
    }
}