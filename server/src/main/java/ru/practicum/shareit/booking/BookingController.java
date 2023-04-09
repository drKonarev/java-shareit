package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDto add(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                  @RequestBody BookingDto booking) {
        return bookingService.save(booking, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto patchStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("bookingId") Long bookingId,
                                          @RequestParam("approved") Boolean approved) {
        return bookingService.updateStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("bookingId") Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }


    @GetMapping
    public List<BookingResponseDto> getAllBookingByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(defaultValue = "ALL", required = false, name = "state") String state,
                                                          @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                          @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllBookingByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                           @RequestParam(defaultValue = "ALL", required = false, name = "state") String state,
                                                           @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                           @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllBookingByOwnerId(ownerId, state, from, size);
    }


}
