package ru.yandex.practicum.exception;

// Слово не найдено в словаре
public class WordNotFoundInDictionaryException extends WordleGameException {
    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}