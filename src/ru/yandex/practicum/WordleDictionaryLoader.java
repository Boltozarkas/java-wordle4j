package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    public WordleDictionary loadDictionary(String filename) throws IOException {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String normalizedWord = WordleDictionary.normalizeWord(line);
                if (normalizedWord.length() == 5) {
                    words.add(normalizedWord);
                }
            }
        }

        if (words.isEmpty()) {
            throw new IOException("Словарь пуст или не содержит слов из 5 букв");
        }

        return new WordleDictionary(words);
    }
}
