package ru.yandex.practicum.exception;

// Базовое непроверяемое исключение для игровых ошибок
public class WordleGameException extends RuntimeException {
    public WordleGameException(String message) {
        super(message);
    }
}