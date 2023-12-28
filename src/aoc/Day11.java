package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day11 {
    static class Galaxy {
        final long xPos;
        final long yPos;
        Galaxy(long x, long y) {
            xPos = x;
            yPos = y;
        }

        long getDistance(Galaxy other) {
            return Math.abs(other.xPos - xPos) + Math.abs(other.yPos - yPos);
        }
    }

    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("data/day11.txt"))) {
            while (scanner.hasNext()) {
                data.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        List<String> expandedUniverse = new ArrayList<>();
        //  expand rows
        for (String line : data) {
            expandedUniverse.add(line);
            if (!line.contains("#")) {
                expandedUniverse.add(line);
            }
        }
        int charIdx = 0;
        while(true) {
            boolean isClear = true;
            for (String s : expandedUniverse) {
                if (s.charAt(charIdx) == '#') {
                    isClear = false;
                    break;
                }
            }
            if (isClear) {
                for (int y = 0; y < expandedUniverse.size(); y++) {
                    String newRow = addSpaceAtIdx(expandedUniverse.get(y), charIdx);
                    expandedUniverse.set(y, newRow);
                }
                charIdx++;
            }
            charIdx++;
            if (charIdx > expandedUniverse.get(0).length() - 1) {
                break;
            }
        }

        //  collect the galaxies
        List<Galaxy> galaxies = new ArrayList<>();
        for (int x = 0; x < expandedUniverse.get(0).length(); x++) {
            for (int y = 0; y < expandedUniverse.size(); y++) {
                if (expandedUniverse.get(y).charAt(x) == '#') {
                    Galaxy galaxy = new Galaxy(x, y);
                    galaxies.add(galaxy);
                }
            }
        }

        long sumOfDistances = 0;
        for (int i = 0; i < galaxies.size(); i++) {
            for (int j = i + 1; j < galaxies.size(); j++) {
                long distance = galaxies.get(i).getDistance(galaxies.get(j));
                sumOfDistances += distance;
            }
        }

        System.out.println("Part 1: Sum of all distances: " + sumOfDistances);
    }

    static String addSpaceAtIdx(String original, int position) {
        int len = original.length();
        char[] newArray = new char[len + 1];
        original.getChars(0, position, newArray, 0);
        newArray[position] = '.';
        original.getChars(position, len, newArray, position + 1);
        return new String(newArray);
    }
}
