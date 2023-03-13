package ru.practicum.shareit.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(ItemRepository.class);


    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository, BookingMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @Override
    public BookingResponseDto save(BookingDto booking, Long bookerId) {
        log.info("Trying to save booking");
        booking.setBookerId(bookerId);
        booking.setStatus(BookingStatus.WAITING);
        checkDate(booking);
        if (booking.getStart().equals(booking.getEnd())) {
            throw new NullPointerException("Time start and time end cannot be equal!");
        }
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> {
            throw new ItemNotFoundException("Item not found");
        });
        User user = userRepository.findById(bookerId).orElseThrow(() -> {
            throw new UserNotFoundException("User not found");
        });

        checkAvailableItem(item);
        validateAccessToPublic(item.getOwner().getId(), bookerId);

        Booking newBooking = bookingRepository.save(mapper.dtoToBooking(booking, user, item));
        log.info("Booking successfully save");
        return mapper.bookingToResponse(newBooking);
    }

    @Override
    public BookingResponseDto updateStatus(Long bookingId, Boolean status, Long userId) {

        if (status == null) throw new IllegalArgumentException("Status cannot be null!");

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new ItemNotFoundException("Booking not found");
        });

        if (booking.getStatus() != null && !booking.getStatus().equals(BookingStatus.WAITING))
            throw new NullPointerException("Cannot change status secondary!");

        validateAccessToProve(booking.getItem().getOwner().getId(), userId);

        if (status) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return mapper.bookingToResponse(booking);
    }

    @Override
    public BookingResponseDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new UserNotFoundException("Booking not found");
        });
        userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User not found");
        });
        validateAccessToGet(booking.getItem().getOwner().getId(), booking.getBooker().getId(), userId);
        return mapper.bookingToResponse(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingByUserId(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User not found");
        });
        List<Booking> allBookingsByUserId = bookingRepository.findAll()
                .stream()
                .filter(booking -> booking.getBooker().getId().equals(userId))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());

        return getFilteredList(allBookingsByUserId, state);
    }

    @Override
    public List<BookingResponseDto> getAllBookingByOwnerId(Long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() -> {
            throw new UserNotFoundException("User not found");
        });
        List<Booking> allBookingsByOwnerId = bookingRepository.findAll()
                .stream()
                .filter(booking -> booking.getItem().getOwner().getId().equals(ownerId))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());

        if (allBookingsByOwnerId.isEmpty()) return new ArrayList<>();

        return getFilteredList(allBookingsByOwnerId, state);
    }


    private void validateAccessToProve(Long ownerId, Long userId) {
        if (!Objects.equals(userId, ownerId))
            throw new AccessOrAvailableException("Недостаточно прав для доступа");

    }

    private void validateAccessToPublic(Long ownerId, Long userId) { // подумать, как сделать лучше, сейчас - не работает
        if (Objects.equals(ownerId, userId))
            throw new AccessOrAvailableException("Нельзя забронировать своою вещь");
    }

    private void checkAvailableItem(Item item) {
        if (!item.isAvailable()) throw new NullPointerException("Item is not available!");
    }

    private void validateAccessToGet(Long ownerId, Long bookerId, Long userId) {
        if (!Objects.equals(ownerId, userId) &&
                !Objects.equals(bookerId, userId))
            throw new AccessOrAvailableException("Нет доступа к просмотру брони!");
    }

    private void checkDate(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()))
            throw new NullPointerException("End date cannot be early then start date!"); // изменить ошибку на время
    }

    private List<BookingResponseDto> getFilteredList(List<Booking> bookings, String state) {
        List<BookingResponseDto> returnedBookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                returnedBookings.addAll(bookings.stream()
                        .map(mapper::bookingToResponse)
                        .collect(Collectors.toList()));
                break;
            case "CURRENT":
                returnedBookings.addAll(bookings.stream()
                        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now())
                                && booking.getStart().isBefore(LocalDateTime.now()))
                        .map(mapper::bookingToResponse)
                        .sorted(Comparator.comparing(BookingResponseDto::getStart))
                        .collect(Collectors.toList()));
                break;
            case "PAST":
                returnedBookings.addAll(bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(mapper::bookingToResponse)
                        .collect(Collectors.toList()));
                break;
            case "FUTURE":
                returnedBookings.addAll(bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .map(mapper::bookingToResponse)
                        .collect(Collectors.toList()));
                break;
            case "REJECTED":
                returnedBookings.addAll(bookings.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .map(mapper::bookingToResponse)
                        .collect(Collectors.toList()));
                break;
            case "WAITING":
                returnedBookings.addAll(bookings.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .map(mapper::bookingToResponse)
                        .collect(Collectors.toList()));
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
        return returnedBookings;
    }
}
