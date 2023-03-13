package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Item {

    private  User owner;

    private long id;

    private  String name;

    private  String description;

    private  boolean available; // true - available

}
