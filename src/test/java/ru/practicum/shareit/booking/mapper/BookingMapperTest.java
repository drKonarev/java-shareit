package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private BookingMapper bookingMapper = new BookingMapper();

    User user = new User(1L, "name", "mail@mail.com");
    Item item = new Item(1L, user, "name", "desc", true, null);

    Booking booking = new Booking(1L, item, user, LocalDateTime.now(),
            LocalDateTime.now().plusHours(2), BookingStatus.WAITING);

    BookingDto bookingDto = new BookingDto(1L, 1L, LocalDateTime.now(),
            LocalDateTime.now().plusHours(2), BookingStatus.WAITING, 1L);

    @Test
    void dtoToBooking() {
        Booking expectedBooking = new Booking(1L, item, user, bookingDto.getStart(),
                bookingDto.getEnd(), BookingStatus.WAITING);

        Booking actualBooking = bookingMapper.dtoToBooking(bookingDto, user, item);

        assertEquals(expectedBooking.toString(), actualBooking.toString());
    }

    @Test
    void bookingToResponse() {
        BookingResponseDto expectedBookingResponseDto = new BookingResponseDto(1L, booking.getStart(),
                booking.getEnd(), BookingStatus.WAITING, user, item);

        BookingResponseDto actualBookingResponseDto = bookingMapper.bookingToResponse(booking);

        assertEquals(expectedBookingResponseDto.toString(), actualBookingResponseDto.toString());

    }

    @Test
    void bookingToItem() {
        BookingItemDto expectedBookingItemDto = new BookingItemDto(1L, 1L);

        BookingItemDto actualBookingItemDto = bookingMapper.bookingToItem(booking);

        assertEquals(expectedBookingItemDto.toString(), actualBookingItemDto.toString());
    }

}