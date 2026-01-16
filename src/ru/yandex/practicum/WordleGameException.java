package ru.yandex.practicum;

// Базовое исключение для игровых ошибок
public class WordleGameException extends Exception {
    public WordleGameException(String message) {
        super(message);
    }
}

// Слово не найдено в словаре
class WordNotFoundInDictionaryException extends WordleGameException {
    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}

// Некорректный ввод пользователя
class InvalidInputException extends WordleGameException {
    public InvalidInputException(String message) {
        super(message);
    }
}

// Неправильная длина слова
class InvalidWordLengthException extends InvalidInputException {
    public InvalidWordLengthException(String message) {
        super(message);
    }
}