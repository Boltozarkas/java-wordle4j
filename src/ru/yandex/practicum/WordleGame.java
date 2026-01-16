package ru.yandex.practicum;

import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {
    private final String answer;
    private int remainingSteps;
    private final WordleDictionary dictionary;
    private final List<String> previousGuesses = new ArrayList<>();
    private final List<String> previousHints = new ArrayList<>();
    private boolean gameFinished = false;

    public WordleGame(WordleDictionary dictionary) {
        this.dictionary = dictionary;
        this.answer = dictionary.getRandomWord();
        this.remainingSteps = 6;
    }

    public WordleGame(WordleDictionary dictionary, String answer) {
        this.dictionary = dictionary;
        this.answer = dictionary.normalizeWord(answer);
        this.remainingSteps = 6;
    }

    public String makeGuess(String guess) throws WordleGameException {
        if (gameFinished) {
            throw new IllegalStateException("Игра уже завершена");
        }

        if (remainingSteps <= 0) {
            throw new IllegalStateException("Закончились попытки");
        }

        String normalizedGuess = dictionary.normalizeWord(guess);

        // Проверка ввода
        if (normalizedGuess.length() != 5) {
            throw new InvalidWordLengthException("Слово должно содержать 5 букв");
        }

        if (!dictionary.contains(normalizedGuess)) {
            throw new WordNotFoundInDictionaryException("Слово не найдено в словаре");
        }

        // Анализ слова
        String hint = dictionary.analyzeWord(normalizedGuess, answer);

        previousGuesses.add(normalizedGuess);
        previousHints.add(hint);
        remainingSteps--;

        // Проверка на победу
        if (normalizedGuess.equals(answer)) {
            gameFinished = true;
        } else if (remainingSteps == 0) {
            gameFinished = true;
        }

        return hint;
    }

    public String getHint() {
        List<String> possibleWords = dictionary.filterWords(previousGuesses, previousHints);

        if (possibleWords.isEmpty()) {
            return "Нет подходящих слов в словаре";
        }

        // Выбираем слово из подходящих
        Random random = new Random();
        return possibleWords.get(random.nextInt(possibleWords.size()));
    }

    public int getRemainingSteps() {
        return remainingSteps;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public boolean isWon() {
        if (!gameFinished) {
            return false;
        }
        return previousGuesses.size() > 0 &&
                previousGuesses.get(previousGuesses.size() - 1).equals(answer);
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getPreviousGuesses() {
        return Collections.unmodifiableList(previousGuesses);
    }

    public List<String> getPreviousHints() {
        return Collections.unmodifiableList(previousHints);
    }
}
