package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ItemDto {

    @NotBlank(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String name;

    @NotBlank(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

    @NotNull(groups = {Update.class})
    private long id;

    @NotNull(groups = {Update.class, Create.class})
    private long ownerId;

    private Long requestId;

}
