package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;


public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        String fileName = readLine("enter fileName: ");
        String sortOptions = readLine("enter sort options: alphabetic, symbolCounts, word ");
        Optional<Integer> n;
        if(sortOptions.equals("word")){
            System.out.print("enter number of word (start from 1): ");
            int number = scanner.nextInt();
            if(number < 0){
                throw new IllegalArgumentException("number should be not negative");
            }
            n = Optional.of(number);
        } else{
            n = Optional.empty();
        }

        String direction = readLine("desc or asc?: ");
        String outputFileName = readLine("enter output filename: ");

        Path path = Paths.get("src/main/resources/" + fileName);

        Map<String, Integer> dict = new HashMap<>();

        Files.lines(path).forEach(line -> dict.merge(line, 1, Integer::sum));

        List<String> sortedLines = dict.keySet().stream().sorted(
                getSortFunction(sortOptions, direction, n)
        ).toList();

        write(sortedLines, dict, outputFileName);
    }

    static Comparator<String> getSortFunction(String sortOptions, String direction, Optional<Integer> n){
        Comparator<String> comparator;
        comparator = switch(sortOptions){
            case "word" -> Comparator.comparing(getThWord(n.get()));
            case "alphabetic" -> Comparator.comparing(Function.identity());
            case "symbolCounts" ->  Comparator.comparingInt(String::length);
            default -> throw new IllegalArgumentException("unknown sort option");
        };

        if(direction.equals("desc")){
            return comparator.reversed();
        } else{
            return comparator;
        }
    }

    static Function<String, String> getThWord(int n){
        return (String line) -> {
            var words = line.split(" ");
            if (words.length > n) {
                return "";
            }
            return words[n - 1];
        };
    }

    static void write(List<String> sortedLines, Map<String, Integer> stringToCount, String fileName) {
        try(BufferedWriter out = new BufferedWriter(new FileWriter(fileName))){
            for(String line: sortedLines) {
                int count = stringToCount.get(line);
                String outputLine = String.format("%s %s", line, count);
                for (int i = 0; i < count; ++i) {
                    out.write(outputLine);
                    out.newLine();
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String readLine(String output){
        System.out.print(output);
        return scanner.nextLine();
    }
}