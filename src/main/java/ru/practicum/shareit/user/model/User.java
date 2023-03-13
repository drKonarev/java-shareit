package ru.practicum.shareit.user.model;

import lombok.*;


/**
 * TODO Sprint add-controllers.
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class User {

    private long id;
    private String name;
    private String email;

}
