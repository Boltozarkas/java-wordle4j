package ru.yandex.practicum;

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
    private static PrintWriter logWriter;

    public static void main(String[] args) {
        try {
            // Создаем лог-файл
            logWriter = new PrintWriter(new FileWriter("wordle.log", StandardCharsets.UTF_8));

            // Загружаем словарь из файла words_ru.txt
            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.loadDictionary("words_ru.txt");

            logInfo("Словарь загружен. Количество слов: " + dictionary.getWords().size());

            // Создаем игру
            WordleGame game = new WordleGame(dictionary);

            Scanner scanner = new Scanner(System.in, "UTF-8");

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
                    String hint = game.getHint();
                    if (hint.equals("Нет подходящих слов в словаре")) {
                        System.out.println("Ошибка: " + hint);
                        logWarning("Нет подходящих слов для подсказки");
                        continue;
                    }

                    System.out.println("Компьютер пытается: " + hint);

                    try {
                        String result = game.makeGuess(hint);
                        System.out.println("Результат: " + result);
                        logInfo("Автоход: " + hint + " -> " + result);

                        if (game.isGameFinished() && game.isWon()) {
                            System.out.println("\nКомпьютер угадал слово!");
                            logInfo("Игра выиграна компьютером. Загаданное слово: " + game.getAnswer());
                        } else if (game.isGameFinished()) {
                            System.out.println("\nИгра окончена. Компьютер проиграл.");
                            System.out.println("Загаданное слово: " + game.getAnswer());
                            logInfo("Игра проиграна компьютером. Загаданное слово: " + game.getAnswer());
                        }

                    } catch (WordleGameException e) {
                        System.out.println("Ошибка при автоходе: " + e.getMessage());
                        logError("Ошибка при автоходе: " + e.getMessage());
                    }

                } else {
                    // Игрок вводит слово
                    try {
                        String result = game.makeGuess(input);
                        System.out.println("Результат: " + result);
                        logInfo("Введено слово: " + input + ", результат: " + result);

                        if (game.isGameFinished() && game.isWon()) {
                            System.out.println("\nПоздравляем! Вы угадали слово!");
                            logInfo("Игра выиграна игроком. Загаданное слово: " + game.getAnswer());
                        } else if (game.isGameFinished()) {
                            System.out.println("\nИгра окончена. Вы проиграли.");
                            System.out.println("Загаданное слово: " + game.getAnswer());
                            logInfo("Игра проиграна игроком. Загаданное слово: " + game.getAnswer());
                        }

                    } catch (WordNotFoundInDictionaryException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                        System.out.println("Попробуйте другое слово.");
                        logWarning("Слово не найдено в словаре: " + input);
                    } catch (InvalidInputException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                        logWarning("Некорректный ввод: " + input);
                    }
                }
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            String errorMessage = "Файл словаря не найден: words_ru.txt";
            logError(errorMessage);
            System.err.println(errorMessage);
            System.err.println("Поместите файл words_ru.txt в ту же папку, где находится программа.");
        } catch (IOException e) {
            logError("Ошибка ввода-вывода: " + e.getMessage());
            System.err.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logError("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace(logWriter);
            System.err.println("Произошла непредвиденная ошибка. Подробности в лог-файле.");
        } finally {
            if (logWriter != null) {
                logWriter.close();
            }
        }
    }

    private static void logInfo(String message) {
        String logMessage = "[INFO] " + message;
        if (logWriter != null) {
            logWriter.println(logMessage);
            logWriter.flush();
        }
    }

    private static void logWarning(String message) {
        String logMessage = "[WARN] " + message;
        if (logWriter != null) {
            logWriter.println(logMessage);
            logWriter.flush();
        }
    }

    private static void logError(String message) {
        String logMessage = "[ERROR] " + message;
        if (logWriter != null) {
            logWriter.println(logMessage);
            logWriter.flush();
        }
    }
}
