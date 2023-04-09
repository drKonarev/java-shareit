package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.markers.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class BookingDto {

    @NotNull(groups = Update.class, message = "Id couldn't be null")
    private Long id;
    @NotNull(message = "ItemId couldn't be null")
    private Long itemId;
    @FutureOrPresent(message = "Start time couldn't be in past")
    @DateTimeFormat(pattern = "yyyy/MM/dd hh:mm:ss")
    private LocalDateTime start;
    @Future(message = "Start time couldn't be in past or present")
    @DateTimeFormat(pattern = "yyyy/MM/dd hh:mm:ss")
    private LocalDateTime end;
    private BookingStatus status;
    private Long bookerId;

}
