package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto save(BookingDto booking, Long userId);

    BookingResponseDto updateStatus(Long bookingId, Boolean status, Long ownerId);

    BookingResponseDto getById(Long bookingId, Long ownerId);

    List<BookingResponseDto> getAllBookingByUserId(Long userId, String state, Integer from, Integer size);

    List<BookingResponseDto> getAllBookingByOwnerId(Long ownerId, String state, Integer from, Integer size);
}
