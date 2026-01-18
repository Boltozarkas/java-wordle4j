package ru.yandex.practicum.exception;

// Некорректный ввод пользователя
public class InvalidInputException extends WordleGameException {
    public InvalidInputException(String message) {
        super(message);
    }
}