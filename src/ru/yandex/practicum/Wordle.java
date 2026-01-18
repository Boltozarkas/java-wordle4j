package ru.yandex.practicum;

import ru.yandex.practicum.exception.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */

// Привет! С новым годом! Держи это безобразие:D
public class Wordle {

    public static void main(String[] args) {
        // Внешний try-catch для обработки критических ошибок
        try (PrintWriter logWriter = new PrintWriter(new FileWriter("wordle.log", StandardCharsets.UTF_8))) {
            // Загружаем словарь из файла words_ru.txt
            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.loadDictionary("words_ru.txt");

            logInfo(logWriter, "Словарь загружен. Количество слов: " + dictionary.getWords().size());

            // Создаем игру
            WordleGame game = new WordleGame(dictionary);

            // Запускаем игровой цикл в отдельном методе
            runGameLoop(game, logWriter);

        } catch (FileNotFoundException e) {
            System.err.println("Файл словаря не найден: words_ru.txt");
            System.err.println("Поместите файл words_ru.txt в ту же папку, где находится программа.");
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
        }
    }

    // Метод для игрового цикла
    private static void runGameLoop(WordleGame game, PrintWriter logWriter) {
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            System.out.println("Добро пожаловать в Wordle!");
            System.out.println("Угадайте слово из 5 букв.");
            System.out.println("У вас есть 6 попыток.");
            System.out.println("После ввода слова вы увидите подсказку:");
            System.out.println("  + - буква на правильной позиции");
            System.out.println("  ^ - буква есть в слове, но на другой позиции");
            System.out.println("  - - буквы нет в слове");
            System.out.println("\nПросто нажимайте Enter, чтобы компьютер играл сам!");
            System.out.println("================================");

            // Игровой цикл
            while (!game.isGameFinished()) {
                System.out.println("\nОсталось попыток: " + game.getRemainingSteps());
                System.out.print("Введите слово или нажмите Enter для автохода: ");

                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    // Компьютер играет сам
                    handleAutoMove(game, logWriter);
                } else {
                    // Игрок вводит слово
                    handlePlayerMove(game, input, logWriter);
                }
            }
        } catch (Exception e) {
            logError(logWriter, "Критическая ошибка в игровом цикле: " + e.getMessage());
            System.err.println("Произошла ошибка во время игры: " + e.getMessage());
        }
    }

    // Обработка автохода (нажатие Enter)
    private static void handleAutoMove(WordleGame game, PrintWriter logWriter) {
        String hint = game.getHint();
        if (hint.equals("Нет подходящих слов в словаре")) {
            System.out.println("Ошибка: " + hint);
            logWarning(logWriter, "Нет подходящих слов для подсказки");
            return;
        }

        System.out.println("Компьютер пытается: " + hint);

        try {
            String result = game.makeGuess(hint);
            System.out.println("Результат: " + result);
            logInfo(logWriter, "Автоход: " + hint + " -> " + result);

            if (game.isGameFinished() && game.isWon()) {
                System.out.println("\nКомпьютер угадал слово!");
                logInfo(logWriter, "Игра выиграна компьютером. Загаданное слово: " + game.getAnswer());
            } else if (game.isGameFinished()) {
                System.out.println("\nИгра окончена. Компьютер проиграл.");
                System.out.println("Загаданное слово: " + game.getAnswer());
                logInfo(logWriter, "Игра проиграна компьютером. Загаданное слово: " + game.getAnswer());
            }

        } catch (WordleGameException e) {
            System.out.println("Ошибка при автоходе: " + e.getMessage());
            logError(logWriter, "Ошибка при автоходе: " + e.getMessage());
        }
    }

    // Обработка хода игрока
    private static void handlePlayerMove(WordleGame game, String input, PrintWriter logWriter) {
        try {
            String result = game.makeGuess(input);
            System.out.println("Результат: " + result);
            logInfo(logWriter, "Введено слово: " + input + ", результат: " + result);

            if (game.isGameFinished() && game.isWon()) {
                System.out.println("\nПоздравляем! Вы угадали слово!");
                logInfo(logWriter, "Игра выиграна игроком. Загаданное слово: " + game.getAnswer());
            } else if (game.isGameFinished()) {
                System.out.println("\nИгра окончена. Вы проиграли.");
                System.out.println("Загаданное слово: " + game.getAnswer());
                logInfo(logWriter, "Игра проиграна игроком. Загаданное слово: " + game.getAnswer());
            }

        } catch (WordNotFoundInDictionaryException e) {
            System.out.println("Ошибка: " + e.getMessage());
            System.out.println("Попробуйте другое слово.");
            logWarning(logWriter, "Слово не найдено в словаре: " + input);
        } catch (InvalidInputException e) {
            System.out.println("Ошибка: " + e.getMessage());
            logWarning(logWriter, "Некорректный ввод: " + input);
        } catch (WordleGameException e) {
            // Ловим общий WordleGameException для других случаев
            System.out.println("Ошибка игры: " + e.getMessage());
            logError(logWriter, "Ошибка игры: " + e.getMessage());
        }
    }

    private static void logInfo(PrintWriter logWriter, String message) {
        String logMessage = "[INFO] " + message;
        if (logWriter != null) {
            logWriter.println(logMessage);
            logWriter.flush();
        }
    }

    private static void logWarning(PrintWriter logWriter, String message) {
        String logMessage = "[WARN] " + message;
        if (logWriter != null) {
            logWriter.println(logMessage);
            logWriter.flush();
        }
    }

    private static void logError(PrintWriter logWriter, String message) {
        String logMessage = "[ERROR] " + message;
        if (logWriter != null) {
            logWriter.println(logMessage);
            logWriter.flush();
        }
    }
}
