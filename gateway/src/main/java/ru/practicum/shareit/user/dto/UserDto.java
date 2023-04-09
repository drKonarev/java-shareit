package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class UserDto {
    private long id;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String name;

    @Email(groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class})
    private String email;


}
