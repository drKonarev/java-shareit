package ru.practicum.shareit.item.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ItemDtoRequest {

    private Long id;

    private String name;

    private String description;

    private boolean available;

    private Long requestId;
}
