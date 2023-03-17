package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {

    public Booking dtoToBooking(BookingDto bookingDto, User owner, Item item) {
        return new Booking(bookingDto.getId(),
                item,
                owner,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getStatus());

    }

    public BookingResponseDto bookingToResponse(Booking booking) {
        return new BookingResponseDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBooker(),
                booking.getItem());
    }

    public BookingItemDto bookingToItem(Booking booking) {
        return new BookingItemDto(booking.getId(), booking.getBooker().getId());

    }
}
