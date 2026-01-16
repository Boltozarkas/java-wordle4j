package ru.yandex.practicum;

// Слово не найдено в словаре
public class WordNotFoundInDictionaryException extends WordleGameException {
    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}