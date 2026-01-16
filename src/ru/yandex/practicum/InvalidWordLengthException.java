package ru.yandex.practicum;

// Неправильная длина слова
public class InvalidWordLengthException extends InvalidInputException {
    public InvalidWordLengthException(String message) {
        super(message);
    }
}