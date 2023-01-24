package ru.practicum.shareit.exception;

public class ObjectDidntFoundException extends RuntimeException {
    public ObjectDidntFoundException(String message) {
        super(message);
    }
}
