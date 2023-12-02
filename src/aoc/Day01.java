package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Day01 {

    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("data/day01.txt"))) {
            while (scanner.hasNext()) {
                data.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        final Map<String, String> numberMap = new HashMap<>();
        numberMap.put("one", "1");
        numberMap.put("two", "2");
        numberMap.put("three", "3");
        numberMap.put("four", "4");
        numberMap.put("five", "5");
        numberMap.put("six", "6");
        numberMap.put("seven", "7");
        numberMap.put("eight", "8");
        numberMap.put("nine", "9");

        long totalPart1 = 0;
        long totalPart2 = 0;
        Pattern digitOnlyPattern = Pattern.compile("\\d");
        Pattern digitAndWordPattern = Pattern.compile("(\\d)|(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)");
        for (final String line : data) {
            //  Part 1
            final String twoDigitsPart1 = getFirstDigit(line, digitOnlyPattern, numberMap) +
                    getLastDigit(line, digitOnlyPattern, numberMap);
            if (!twoDigitsPart1.isEmpty()) {
                totalPart1 += Long.parseLong(twoDigitsPart1);
            }

            //  Part 2
            final String twoDigitsPart2 = getFirstDigit(line, digitAndWordPattern, numberMap) +
                    getLastDigit(line, digitAndWordPattern, numberMap);
            totalPart2 += Long.parseLong(twoDigitsPart2);

        }

        System.out.println("Part 1: " + totalPart1);
        System.out.println("Part 2: " + totalPart2);
    }

    static String getFirstDigit(String line,
                                Pattern pattern,
                                Map<String, String> numberMap) {
        for (int i = 0; i < line.length(); i++) {
            String substring = line.substring(i);
            String match = matchSubstring(substring, pattern, numberMap);
            if (match == null) {
                continue;
            }
            return match;
        }
        System.out.println("Error finding first digit: " + line);
        return "";
    }

    static String getLastDigit(String line,
                               Pattern pattern,
                               Map<String, String> numberMap) {
        for (int i = line.length() - 1; i >= 0; i--) {
            String substring = line.substring(i);
            String match = matchSubstring(substring, pattern, numberMap);
            if (match == null) {
                continue;
            }
            return match;
        }
        System.out.println("Error finding last digit: " + line);
        return "";
    }

    static String matchSubstring(String substring,
                                 Pattern pattern,
                                 Map<String, String> numberMap) {
        final Matcher m = pattern.matcher(substring);
        if (m.find()) {
            if (Character.isDigit(m.group().charAt(0))) {
                return m.group();
            } else {
                return numberMap.get(m.group());
            }
        }
        return null;
    }
}
