package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day05 {
    public static class Range {
        final long destStart;
        final long sourceStart;
        final long length;

        final long destEnd;
        final long sourceEnd;

        Range(long destStart, long sourceStart, long length) {
            this.destStart = destStart;
            this.sourceStart = sourceStart;
            this.length = length;

            //  this just makes things less messy
            this.destEnd = this.destStart + length - 1;
            this.sourceEnd = this.sourceStart + length - 1;
        }

        @Override
        public String toString() {
            return("D: " + this.destStart + " S: " + this.sourceStart + " L: " + this.length);
        }
    }
    public static class FarmMap {
        final String source;
        final String dest;
        final List<Range> ranges;
        final List<Range> splitRanges;

        FarmMap(String source, String dest) {
            this.source = source;
            this.dest = dest;
            this.ranges = new ArrayList<>();
            this.splitRanges = new ArrayList<>();
        }

        @Override
        public String toString() {
            String output = source + "-to-" + dest + ":";
            output += "\n";
            for (Range range : splitRanges) {
                output += range + "\n";
            }
            return output;
        }
    }

    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("testData/day05.txt"))) {
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

        //  Part 2

        almanac.get("humidity").splitRanges.addAll(almanac.get("humidity").ranges);
        sortSplitRangesBySource(almanac.get("humidity"));
        List<Range> newRanges = splitUpperRanges(almanac.get("temperature").ranges, almanac.get("humidity").splitRanges);
        int jennifer = 9;
    }

    static Long convertSeedToLocation(final Map<String, FarmMap> almanac, Long value, String entry) {
        FarmMap currentMap = almanac.get(entry);
        if (entry.equals("location")) {
            return value;
        }

        for(Range range : currentMap.ranges) {
            if (value >= range.sourceStart && value <= range.sourceEnd) {
                return convertSeedToLocation(almanac, range.destStart + value - range.sourceStart, currentMap.dest);
            }
        }

        return (convertSeedToLocation(almanac, value, currentMap.dest));
    }

    static void sortSplitRangesByDestination(FarmMap map) {
        Collections.sort(map.splitRanges, Comparator.comparing(range -> range.destStart));
    }

    static void sortSplitRangesBySource(FarmMap map) {
        Collections.sort(map.splitRanges, Comparator.comparing(range -> range.sourceStart));
    }

    static List<Range> splitUpperRanges(List<Range> upperRanges, List<Range> lowerRanges) {
        List<Range> newRanges = new ArrayList<>();

        for (Range upperRange : upperRanges) {
            System.out.println("Upper range: " + upperRange);
            boolean didSplitRange = false;
            for (Range lowerRange : lowerRanges) {
                //  we've sorted the ranges
                //  we can skip ranges until we get high enough
                if (upperRange.destEnd < lowerRange.sourceStart) {
                    System.out.println("Too low: " + lowerRange);
                    break;
                }

                //  upper dest range fully inside lower source range
                //  no need to split
                if (upperRange.destStart >= lowerRange.sourceStart && upperRange.destEnd <= lowerRange.sourceEnd) {
                    newRanges.add(upperRange);
                    System.out.println("Fits inside: " + lowerRanges);
                    break;
                }

                //  upper dest range starts below within lower source range but ends within it
                if (upperRange.destStart < lowerRange.sourceStart && upperRange.destEnd >= lowerRange.sourceStart) {
                    final long newLength = lowerRange.sourceStart - upperRange.destStart;
                    final Range first = new Range(upperRange.destStart, upperRange.sourceStart, newLength);
                    final Range second = new Range(lowerRange.sourceStart, upperRange.sourceStart + newLength, upperRange.length - newLength);
                    newRanges.add(first);
                    newRanges.add(second);
                    System.out.println("Splitting: " + lowerRange);
                    didSplitRange = true;
                    break;
                }

                //  upper dest range starts within lower source range but ends beyond it
                //  need to split
                if (upperRange.destStart >= lowerRange.sourceStart && upperRange.destEnd > lowerRange.sourceEnd) {
                    System.out.println("Now we have to do something: " + lowerRange);
                    final long newLength = lowerRange.sourceEnd - upperRange.destEnd;
                    Range first = new Range(upperRange.destStart, upperRange.sourceStart, newLength);
                    Range second = new Range(lowerRange.sourceEnd + 1, upperRange.sourceStart + newLength, upperRange.length - newLength);
                    newRanges.add(first);
                    newRanges.add(second);
                    didSplitRange = true;
                    break;
                }
            }
            if (!didSplitRange) {
                System.out.println("No match; keep as is");
                newRanges.add(upperRange);
            }
        }
        return newRanges;
    }
}
