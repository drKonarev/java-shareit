package ru.practicum.shareit.error;

public class MyValidationException extends RuntimeException {

    public MyValidationException(String message) {
        super(message);
    }
}
