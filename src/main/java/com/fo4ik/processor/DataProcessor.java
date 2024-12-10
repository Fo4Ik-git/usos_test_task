package com.fo4ik.processor;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

public class DataProcessor {

    // Method to calculate the number of common letters between two strings
    public static Map<Character, Integer> commonLetters(String str1, String str2) {
        Map<Character, Integer> letterCount1 = countLetters(str1);
        Map<Character, Integer> letterCount2 = countLetters(str2);

        Map<Character, Integer> commonCounts = new HashMap<>();
        for (Map.Entry<Character, Integer> entry : letterCount1.entrySet()) {
            char letter = entry.getKey();
            int count1 = entry.getValue();
            int count2 = letterCount2.getOrDefault(letter, 0);
            if (count2 > 0) {
                commonCounts.put(letter, Math.min(count1, count2));
            }
        }
        return commonCounts;
    }

    // Helper method to count occurrences of each letter in a string
    private static Map<Character, Integer> countLetters(String str) {
        Map<Character, Integer> letterCount = new HashMap<>();
        for (char c : str.toLowerCase().toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount.put(c, letterCount.getOrDefault(c, 0) + 1);
            }
        }
        return letterCount;
    }



}
