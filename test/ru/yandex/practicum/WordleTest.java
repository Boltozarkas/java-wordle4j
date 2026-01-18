package ru.yandex.practicum;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.exception.*;

import java.io.*;
import java.util.*;

class WordleTest {
    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeEach
    void setUp() {
        // Для тестов создаем тестовый словарь из 5-буквенных слов
        List<String> words = Arrays.asList(
                "столы", "стула", "окнаа", "дверь", "книга",
                "ручка", "бумаг", "компы", "мышка", "клава",
                "апрел", "банан", "вишня", "груша", "дыняк"
        );
        dictionary = new WordleDictionary(words);
        game = new WordleGame(dictionary, "столы");
    }

    @Test
    void testDictionaryNormalization() {
        assertEquals("еж", WordleDictionary.normalizeWord("Ёж"));
        assertEquals("елка", WordleDictionary.normalizeWord("Ёлка"));
        assertEquals("слово", WordleDictionary.normalizeWord("Слово "));
    }

    @Test
    void testWordAnalysis() {
        // Тестируем анализ слов
        assertEquals("+++++", dictionary.analyzeWord("столы", "столы")); // Все буквы совпадают

        // Анализируем "стула" против "столы"
        String result = dictionary.analyzeWord("стула", "столы");
        assertEquals("++-+-", result);
    }

    @Test
    void testWordAnalysisExamples() {
        // Пример из задания
        String result = dictionary.analyzeWord("гонец", "герой");
        assertEquals("+^-^-", result);
    }

    @Test
    void testMakeGuess() {
        String result = game.makeGuess("стула");
        assertNotNull(result);
        assertEquals(5, game.getRemainingSteps());
    }

    @Test
    void testMakeGuessInvalidLength() {
        // Без лямбда-выражения
        try {
            game.makeGuess("стол");
            fail("Должно было выбросить исключение");
        } catch (InvalidWordLengthException e) {
            // Ожидаемое исключение
        } catch (WordleGameException e) {
            fail("Неожиданное исключение: " + e.getClass());
        }
    }

    @Test
    void testMakeGuessWordNotFound() {
        // Без лямбда-выражения
        try {
            game.makeGuess("абвгд");
            fail("Должно было выбросить исключение");
        } catch (WordNotFoundInDictionaryException e) {
            // Ожидаемое исключение
        } catch (WordleGameException e) {
            fail("Неожиданное исключение: " + e.getClass());
        }
    }

    @Test
    void testWinGame() {
        game.makeGuess("столы");
        assertTrue(game.isGameFinished());
        assertTrue(game.isWon());
    }

    @Test
    void testLoseGame() {
        for (int i = 0; i < 6; i++) {
            game.makeGuess("стула");
        }
        assertTrue(game.isGameFinished());
        assertFalse(game.isWon());
    }

    @Test
    void testGetHint() {
        String hint = game.getHint();
        assertNotNull(hint);
        assertNotEquals("Нет подходящих слов в словаре", hint);
        assertEquals(5, hint.length());

        // Сделаем ход и проверим подсказку
        game.makeGuess("стула");
        hint = game.getHint();
        assertNotNull(hint);
        assertEquals(5, hint.length());
    }

    @Test
    void testAutoPlayWithEnter() {
        // Тестируем, что при пустом вводе (Enter) используется подсказка
        WordleDictionary smallDict = new WordleDictionary(Arrays.asList("столы", "стула", "окнаа"));
        WordleGame autoGame = new WordleGame(smallDict, "столы");

        // Симулируем нажатие Enter - получаем подсказку и используем её
        String hint = autoGame.getHint();
        assertNotNull(hint);

        // Используем подсказку как ход
        String result = autoGame.makeGuess(hint);
        assertNotNull(result);

        // Проверяем, что ход засчитан
        assertEquals(5, autoGame.getRemainingSteps());
    }

    @Test
    void testHintReturnsValidWord() {
        // Подсказка должна возвращать существующее слово из словаря
        String hint = game.getHint();
        assertTrue(dictionary.contains(hint));
    }

    @Test
    void testRepeatedLetters() {
        // Тест с повторяющимися буквами
        WordleDictionary testDict = new WordleDictionary(Arrays.asList("банан", "набор"));
        String result = testDict.analyzeWord("банан", "набор");
        assertNotNull(result);
    }

    @Test
    void testLoadFromActualFile() throws IOException {
        WordleDictionaryLoader loader = new WordleDictionaryLoader();

        // Проверяем, что файл существует
        File file = new File("words_ru.txt");
        if (file.exists()) {
            WordleDictionary dict = loader.loadDictionary("words_ru.txt");
            assertFalse(dict.getWords().isEmpty());
            System.out.println("Загружено слов: " + dict.getWords().size());
        } else {
            System.out.println("Файл words_ru.txt не найден, тест пропущен");
        }
    }
}
