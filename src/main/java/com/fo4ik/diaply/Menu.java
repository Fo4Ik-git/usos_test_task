package com.fo4ik.diaply;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fo4ik.fetcher.DataFetcher;
import com.fo4ik.processor.DataProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Menu {
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
            System.out.println("╔═══════════════════════╗");
            System.out.println("║        Menu           ║");
            System.out.println("╠═══════════════════════╣");
            System.out.println("║ 1 - Update Data       ║");
            System.out.println("║ 2 - Process           ║");
            System.out.println("║ 0 - Exit              ║");
            System.out.println("╚═══════════════════════╝");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    DataFetcher fetcher = new DataFetcher();
                    fetcher.fetchData();
                    showMenu();
                    break;
                case 2:
                    processData();
                    showMenu();
                    break;
                case 0:
                    System.out.println("Exiting program...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please choose again.");
            }
    }

    private void processData() {
        // Load data from JSON file
        List<Map<String, String>> subjects;
        ObjectMapper mapper = new ObjectMapper();
        try {
            subjects = mapper.readValue(new File("data.json"), new TypeReference<>() {});
        } catch (Exception e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            return;
        }

        // Executor for parallel processing
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
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

            List<Map.Entry<String, Integer>> rankedSubjects = futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                    .collect(Collectors.toList());

            // Print the top 10 subjects
            System.out.println("Top 10 subjects with the most common letters:");
            rankedSubjects.stream().limit(10).forEach(entry -> {
                String[] parts = entry.getKey().split(" \\| ");
                String subjectName = parts[0];
                String instructorName = parts[1];
                System.out.println("Name of Subject: " + subjectName);
                System.out.println("Names: " + instructorName);
                System.out.println("Total Common Letters: " + entry.getValue());
                System.out.println("------------");
            });
        } finally {
            executor.shutdown();
        }
    }
}
