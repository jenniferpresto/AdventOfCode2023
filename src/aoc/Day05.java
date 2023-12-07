package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Day05 {
    public static class Range {
        final long destStart;
        final long sourceStart;
        final long length;

        Range(long destStart, long sourceStart, long length) {
            this.destStart = destStart;
            this.sourceStart = sourceStart;
            this.length = length;
        }
    }
    public static class FarmMap {
        final String source;
        final String dest;
        final List<Range> ranges;

        FarmMap(String source, String dest) {
            this.source = source;
            this.dest = dest;
            this.ranges = new ArrayList<>();
        }
    }
    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("data/day05.txt"))) {
            while (scanner.hasNext()) {
                data.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        //  Create the Almanac
        final List<Long> seeds = new ArrayList<>();
        final Map<String, FarmMap> almanac = new HashMap<>();
        String currentEntry = "";
        for (String line : data) {
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("seeds: ")) {
                String substring = line.substring(7);
                String[] seedStrs = substring.split(" ");
                for (String seedStr : seedStrs) {
                    seeds.add(Long.parseLong(seedStr));
                }
                continue;
            }

            if (Character.isDigit(line.charAt(0))) {
                if (currentEntry.isEmpty() || !almanac.containsKey(currentEntry)) {
                    System.out.println("Uh-oh");
                    break;
                }
                String[] values = line.split(" ");
                Range range = new Range(Long.parseLong(values[0]), Long.parseLong(values[1]), Long.parseLong(values[2]));
                almanac.get(currentEntry).ranges.add(range);

            } else {
                String[] items = line.split("-|\\s+");
                currentEntry = items[0];
                FarmMap farmMap = new FarmMap(items[0], items[2]);
                almanac.put(items[0], farmMap);
            }
        }

        //  Part 1
        Long closestLocation = Long.MAX_VALUE;
        for (Long seed : seeds) {
            Long newLocation = convertSeedToLocation(almanac, seed, "seed");
            if (newLocation < closestLocation) {
                closestLocation = newLocation;
            }
        }
        System.out.println("Part 1: Closest location is " + closestLocation);
    }

    static Long convertSeedToLocation(final Map<String, FarmMap> almanac, Long value, String entry) {
        FarmMap currentMap = almanac.get(entry);
        if (entry.equals("location")) {
            return value;
        }

        for(Range range : currentMap.ranges) {
            if (value >= range.sourceStart && value < range.sourceStart + range.length) {
                return convertSeedToLocation(almanac, range.destStart + value - range.sourceStart, currentMap.dest);
            }
        }

        return (convertSeedToLocation(almanac, value, currentMap.dest));
    }
}