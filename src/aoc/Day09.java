package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day09 {
    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("data/day09.txt"))) {
            while (scanner.hasNext()) {
                data.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        List<List<Long>> sequences = new ArrayList<>();
        for (String line : data) {
            List<Long> sequence = new ArrayList<>();
            String[] numbers = line.split("\\s");
            for (String numStr : numbers) {
                Long num = Long.parseLong(numStr);
                sequence.add(num);
            }
            sequences.add(sequence);
        }

        Long totalPartOne = 0L;
        for (List<Long> sequence : sequences) {
            totalPartOne += getNextValue(sequence, true);
        }
        System.out.println("Part 1 total: " + totalPartOne);

        Long totalPartTwo = 0L;
        for (List<Long> sequence : sequences) {
            totalPartTwo += getNextValue(sequence, false);
        }
        System.out.println("Part 2 total: " + totalPartTwo);
    }

    static Long getNextValue(List<Long> sequence, boolean isPartOne) {
        List<Long> nextRow = new ArrayList<>();
        boolean allZeroes = true;
        for (int i = 0; i < sequence.size() - 1; i++) {
            Long num = sequence.get(i+1) - sequence.get(i);
            if (!num.equals(0L)) {
                allZeroes = false;
            }
            nextRow.add(sequence.get(i + 1) - sequence.get(i));
        }
        if (allZeroes) {
            if (isPartOne) {
                return sequence.get(sequence.size() - 1);
            } else {
                return sequence.get(0);
            }
        }
        if (isPartOne) {
            return sequence.get(sequence.size() - 1) + getNextValue(nextRow, true);
        } else {
            return sequence.get(0) - getNextValue(nextRow, false);
        }
    }
}
