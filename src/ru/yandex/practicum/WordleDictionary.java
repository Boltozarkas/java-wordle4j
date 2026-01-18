package ru.yandex.practicum;

import java.util.List;
import java.util.*;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {
    // Константы
    public static final int WORD_LENGTH = 5;
    private static final char EXACT_MATCH = '+';
    private static final char PARTIAL_MATCH = '^';
    private static final char NO_MATCH = '-';

    private final List<String> words;
    private final Random random = new Random();

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>();
        // Фильтруем только 5-буквенные слова при создании
        for (String word : words) {
            String normalized = normalizeWord(word);
            if (normalized.length() == WORD_LENGTH) {
                this.words.add(normalized);
            }
        }
    }

    public static String normalizeWord(String word) {
        if (word == null) return "";
        return word.trim().toLowerCase().replace('ё', 'е');
    }

    public List<String> getWords() {
        return Collections.unmodifiableList(words);
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
        return words.get(random.nextInt(words.size()));
    }

    public boolean contains(String word) {
        String normalizedWord = normalizeWord(word);
        return words.contains(normalizedWord);
    }

    // Основной метод анализа слов
    public String analyzeWord(String guess, String answer) {
        guess = normalizeWord(guess);
        answer = normalizeWord(answer);

        char[] result = new char[WORD_LENGTH];
        Arrays.fill(result, NO_MATCH);

        // Сначала обрабатываем зеленые (точные совпадения)
        boolean[] answerUsed = new boolean[WORD_LENGTH];
        boolean[] guessProcessed = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = EXACT_MATCH;
                answerUsed[i] = true;
                guessProcessed[i] = true;
            }
        }

        // Затем обрабатываем желтые (есть, но не на месте)
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guessProcessed[i]) {
                continue;
            }

            char currentChar = guess.charAt(i);

            // Ищем эту букву в ответе на еще неиспользованных позициях
            for (int j = 0; j < WORD_LENGTH; j++) {
                if (!answerUsed[j] && answer.charAt(j) == currentChar) {
                    result[i] = PARTIAL_MATCH;
                    answerUsed[j] = true;
                    break;
                }
            }
        }

        return new String(result);
    }

    // Фильтрация слов по текущим подсказкам
    public List<String> filterWords(List<String> previousGuesses, List<String> previousHints) {
        List<String> filteredWords = new ArrayList<>(words);

        for (int i = 0; i < previousGuesses.size(); i++) {
            String guess = normalizeWord(previousGuesses.get(i));
            String hint = previousHints.get(i);

            Iterator<String> iterator = filteredWords.iterator();
            while (iterator.hasNext()) {
                String word = iterator.next();
                if (!matchesHint(word, guess, hint)) {
                    iterator.remove();
                }
            }
        }

        return filteredWords;
    }

    private boolean matchesHint(String word, String guess, String hint) {
        word = normalizeWord(word);

        // Проверяем точные совпадения
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (hint.charAt(i) == EXACT_MATCH) {
                if (word.charAt(i) != guess.charAt(i)) {
                    return false;
                }
            }
        }

        // Проверяем желтые совпадения (есть, но не на месте)
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (hint.charAt(i) == PARTIAL_MATCH) {
                char guessChar = guess.charAt(i);

                // Не может быть на этой же позиции
                if (word.charAt(i) == guessChar) {
                    return false;
                }

                // Должна быть где-то в слове
                boolean found = false;
                for (int j = 0; j < WORD_LENGTH; j++) {
                    if (j != i && word.charAt(j) == guessChar) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }

        // Проверяем серые (отсутствующие буквы)
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (hint.charAt(i) == NO_MATCH) {
                char guessChar = guess.charAt(i);

                // Считаем, сколько раз эта буква встречается в guess
                // и сколько из них помечены как '+'
                int exactMatchesInGuess = 0;
                for (int j = 0; j < WORD_LENGTH; j++) {
                    if (guess.charAt(j) == guessChar && hint.charAt(j) == EXACT_MATCH) {
                        exactMatchesInGuess++;
                    }
                }

                // Считаем, сколько раз эта буква встречается в word
                int countInWord = 0;
                for (int j = 0; j < WORD_LENGTH; j++) {
                    if (word.charAt(j) == guessChar) {
                        countInWord++;
                    }
                }

                // В слове может быть только столько этих букв,
                // сколько точных совпадений в guess
                if (countInWord > exactMatchesInGuess) {
                    return false;
                }
            }
        }

        return true;
    }
}