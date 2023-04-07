package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTest {

    @Autowired
    private final MockMvc mvc;

    @Autowired
    private final ObjectMapper mapper;

    @MockBean
    private final BookingService bookingService;
    User user = new User(1L, "name", "email@mail.com");

    Item item = new Item(1L, user, "itemName", "itemDesc", true, null);
    BookingResponseDto bookingResponseDto = new BookingResponseDto(
            1L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2),
            BookingStatus.WAITING,
            user,
            item);

    BookingDto bookingDto = new BookingDto(
            1L,
            1L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2),
            BookingStatus.WAITING,
            1L);

    @SneakyThrows
    @Test
    void addBooking_whenBookingValid_thenReturnedStatusOk() {
        when(bookingService.save(any(), anyLong()))
                .thenReturn(bookingResponseDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L));

        verify(bookingService).save(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void addBooking_whenBookingNonValid_thenReturnedStatusBadRequest() {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());


        verify(bookingService, never()).save(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void patchStatus_thenReturnedStatusOk() {
        BookingResponseDto updatedBookingResponseDto = bookingResponseDto;
        updatedBookingResponseDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.updateStatus(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(updatedBookingResponseDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService).updateStatus(anyLong(), anyBoolean(), anyLong());
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService).getById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllBookingByUserId_whenStateIsNotRequired_thenDefaultValueIsAll() {
        when(bookingService.getAllBookingByUserId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingResponseDto));

        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].booker.id").value("1"))
                .andExpect(jsonPath("$[0].item.id").value(1L));

        verify(bookingService).getAllBookingByUserId(anyLong(), eq("ALL"), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllBookingByOwnerId() {
        when(bookingService.getAllBookingByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingResponseDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].booker.id").value("1"))
                .andExpect(jsonPath("$[0].item.id").value(1L));

        verify(bookingService).getAllBookingByOwnerId(eq(1L), eq("ALL"), anyInt(), anyInt());
    }
}