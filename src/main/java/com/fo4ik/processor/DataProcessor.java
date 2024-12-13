package com.fo4ik.processor;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class DataProcessor {

    private static Map<Character, Integer> commonLetters(String str1, String str2) {
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

    public void processData() {
        List<Map<String, String>> subjects = loadDataFromFile("data.json");
        if (subjects == null) return;

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            List<Future<Map.Entry<String, Integer>>> futures = submitTasks(subjects, executor);
            List<Map.Entry<String, Integer>> rankedSubjects = collectResults(futures);
            printTopSubjects(rankedSubjects, 10);
        } finally {
            executor.shutdown();
        }
    }

    private static Map<Character, Integer> countLetters(String str) {
        Map<Character, Integer> letterCount = new HashMap<>();
        for (char c : str.toLowerCase().toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount.put(c, letterCount.getOrDefault(c, 0) + 1);
            }
        }
        return letterCount;
    }

    private List<Map<String, String>> loadDataFromFile(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(fileName), new TypeReference<>() {
            });
        } catch (Exception e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            return null;
        }
    }


    private List<Future<Map.Entry<String, Integer>>> submitTasks(List<Map<String, String>> subjects, ExecutorService executor) {
        List<Future<Map.Entry<String, Integer>>> futures = new ArrayList<>();
        for (Map<String, String> subject : subjects) {
            String subjectName = subject.get("nazwaPrzedmiotu");
            String instructorName = subject.get("prowadzacyGrup");
            futures.add(executor.submit(() -> {
                Map<Character, Integer> commonCounts = DataProcessor.commonLetters(subjectName, instructorName);
                int totalCommonCount = commonCounts.values().stream().mapToInt(Integer::intValue).sum();
                return Map.entry(subjectName + " | " + instructorName, totalCommonCount);
            }));
        }
        return futures;
    }

    private List<Map.Entry<String, Integer>> collectResults(List<Future<Map.Entry<String, Integer>>> futures) {
        return futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
    }

    private void printTopSubjects(List<Map.Entry<String, Integer>> rankedSubjects, int limit) {
        System.out.println("Top " + limit + " subjects with the most common letters:");
        rankedSubjects.stream().limit(limit).forEach(entry -> {
            String[] parts = entry.getKey().split(" \\| ");
            String subjectName = parts[0];
            String instructorName = parts[1];
            System.out.println("Name of Subject: " + subjectName);
            System.out.println("Names: " + instructorName);
            System.out.println("Total Common Letters: " + entry.getValue());
            System.out.println("------------");
        });
    }

}
