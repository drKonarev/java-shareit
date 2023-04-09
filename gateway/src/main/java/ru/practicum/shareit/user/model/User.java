package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class User {
    private Long id;

    @NotNull
    private String name;
    @Email(message = "Wrong email format!")
    private String email;

}
