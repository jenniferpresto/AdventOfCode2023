package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day04 {
    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("data/day04.txt"))) {
            while (scanner.hasNext()) {
                data.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        //  Part 1
        long totalPoints = 0;
        for (String line : data) {
            String winnerStr = line.substring(line.indexOf(":\\s+") + 2, line.indexOf(" |"));
            String elfCardStr = line.substring(line.indexOf("| ") + 2);
            String[] winners = winnerStr.split("\\s+");
            String[] elfCard = elfCardStr.split("\\s+");

            Set<String> winnerSet = new HashSet<>(Arrays.asList(winners));
            Set<String> elfSet = new HashSet<>(Arrays.asList(elfCard));

            System.out.println(winnerSet.size());
            System.out.println(elfSet.size());

            Set<String> winningNumbers = elfSet.stream()
                    .filter(winnerSet::contains)
                    .collect(Collectors.toSet());

            int count = winningNumbers.size();
            if (count > 0) {
                totalPoints += 1L << (count - 1);
            }
        }
        
        //  Part 2


        System.out.println("Part 1: Total points: " + totalPoints);

    }
}
