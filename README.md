# Project: Course Finder at UJ

## Project Description

The goal of this project is to find 10 courses offered in the current semester at the Jagiellonian University, whose Polish names share the most letters with the first and last name of the instructor of the exercise group. The order of letters does not matter. If the same letter appears multiple times, we pair as many copies as we find counterparts, e.g., "Krak" has one common letter with the word "kot" and two common letters with the word "kokonik".

## Requirements

- Java 17 or newer
- Gradle 8.0 or newer

## Running Instructions

1. Clone the repository:
   ```sh
   git clone <REPOSITORY_URL>
   cd <REPOSITORY_NAME>
   ```

2. Build the project using Gradle:
   ```sh
   ./gradlew build
   ```

3. Run the application:
   ```sh
   ./gradlew run
   ```

## Code Structure

- `Main.java`: The main class of the application, contains the `main` method.
- `DataFetcher.java`: Class responsible for fetching course data.
- `DataProcessor.java`: Class responsible for processing instructor data.
