package ru.practicum.shareit.request.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ItemRequestDto {

    private Long id;

    @NotNull
    @NotBlank
    private String description;

    @DateTimeFormat(pattern = "yyyy/MM/dd hh:mm:ss")
    private LocalDateTime created = LocalDateTime.now();

    private List<ItemDtoRequest> items = new ArrayList<>();



}
