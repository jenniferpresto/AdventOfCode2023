package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

        long totalPoints = 0;
        final Map<Integer, Long> numCards = new HashMap<>();
        final Map<Integer, Integer> numWinningNumbers = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            numCards.put(i + 1, 1L);
            numWinningNumbers.put(i + 1, 0);
        }

        int cardNum = 1;
        for (String line : data) {
            final String winnerStr = line.substring(line.indexOf(":\\s+") + 2, line.indexOf(" |"));
            final String elfCardStr = line.substring(line.indexOf("| ") + 2);
            final String[] winners = winnerStr.split("\\s+");
            final String[] elfCard = elfCardStr.split("\\s+");

            final Set<String> winnerSet = new HashSet<>(Arrays.asList(winners));
            final Set<String> elfSet = new HashSet<>(Arrays.asList(elfCard));

            //  Part 1
            final Set<String> winningNumbers = elfSet.stream()
                    .filter(winnerSet::contains)
                    .collect(Collectors.toSet());

            int count = winningNumbers.size();
            if (count > 0) {
                totalPoints += 1L << (count - 1);
            }

            //  Part 2
            numWinningNumbers.put(cardNum, count);
            cardNum++;
        }

        //  Part 2 continued
        for (int i = 1; i < numWinningNumbers.size() + 1; i++) {
            for (int j = 0; j < numWinningNumbers.get(i); j++) {
                int cardCopyNum =  i + 1 + j;
                numCards.put(cardCopyNum, numCards.get(cardCopyNum) + numCards.get(i));
            }
        }

        long totalScratchCards = 0;
        for (Map.Entry<Integer, Long> card : numCards.entrySet()) {
            totalScratchCards += card.getValue();
        }

        System.out.println("Part 1: Total points: " + totalPoints);
        System.out.println("Part 2: Total number of cards: " + totalScratchCards);

    }
}
