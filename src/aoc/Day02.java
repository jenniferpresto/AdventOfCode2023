package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day02 {

    public static class Round {
        int r;
        int g;
        int b;

        public Round() {}

        public Round(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public int getPower() {
            return this.r * this.g * this.b;
        }
    }

    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("data/day02.txt"))) {
            while (scanner.hasNext()) {
                data.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        List<List<Round>> games = new ArrayList<>();
        for (String line : data) {
            List<Round> rounds = parseLine(line);
            games.add(rounds);
        }

        //  Part 1
        int total = 0;
        for (int i = 0; i < games.size(); i++) {
            boolean isPossible = true;
            for (Round round : games.get(i)) {
                if (round.r > 12 || round.g > 13 || round.b > 14) {
                    isPossible = false;
                    break;
                }
            }
            if (!isPossible) {
                continue;
            }
            total += (i + 1);
        }
        System.out.println("Total for Part 1 is " + total);

        //  Part 2
        int totalPart2 = 0;
        for (List<Round> game : games) {
            Round minValues = new Round();
            for (Round round : game) {
                if (round.r > minValues.r) {
                    minValues.r = round.r;
                }
                if (round.g > minValues.g) {
                    minValues.g = round.g;
                }
                if (round.b > minValues.b) {
                    minValues.b = round.b;
                }
            }
            int power = minValues.getPower();
            totalPart2 += power;
        }
        System.out.println("Part 2: Power of all games: " + totalPart2);
    }

    static List<Round> parseLine(String line) {
        String[] rounds = line.split("(: )|(; )");
        List<Round> parsedRounds = new ArrayList<>();

        for (int i = 1; i < rounds.length; i++) {
            Round round = new Round();
            String[] allCubes = rounds[i].split(", ");
            for (String cubes : allCubes) {
                String[] numAndColor = cubes.split(" ");
                switch (numAndColor[1]) {
                    case "red" -> round.r = Integer.parseInt(numAndColor[0]);
                    case "green" -> round.g = Integer.parseInt(numAndColor[0]);
                    case "blue" -> round.b = Integer.parseInt(numAndColor[0]);
                    default -> System.out.println("Uh-oh");
                }
            }
            parsedRounds.add(round);
        }
        return parsedRounds;
    }
}
