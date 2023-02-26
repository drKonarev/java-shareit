package ru.practicum.shareit.error;

public class PostAlreadyExistException extends RuntimeException {

    public PostAlreadyExistException(String message) {
        super(message);
    }
}
