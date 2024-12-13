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
                    DataProcessor dataProcessor = new DataProcessor();
                    dataProcessor.processData();
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


}
