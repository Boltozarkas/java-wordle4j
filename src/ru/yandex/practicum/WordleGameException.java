package ru.yandex.practicum;

// Базовое исключение для игровых ошибок
public class WordleGameException extends Exception {
    public WordleGameException(String message) {
        super(message);
    }
}
