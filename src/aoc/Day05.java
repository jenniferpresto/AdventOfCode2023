package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

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

        public boolean sourceIsWithinRange(long source) {
            return source >= this.sourceStart && source <= this.sourceEnd;
        }

        @Override
        public String toString() {
            return ("D: "
                    + this.destStart
                    + ".."
                    + this.destEnd
                    + " S: "
                    + this.sourceStart
                    + ".."
                    + this.sourceEnd
                    + " L: "
                    + this.length);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || o.getClass() != getClass()) {
                return false;
            }

            final Range other = (Range) o;
            return this.destStart == other.destStart
                    && this.sourceStart == other.sourceStart
                    && this.length == other.length;
        }

        @Override
        public int hashCode() {
            return Objects.hash(destStart, sourceStart, length);
        }
    }

    public static class FarmMap {
        final String source;
        final String dest;
        final List<Range> originalRanges;
        final List<Range> ranges;
        final List<Range> splitRanges;

        FarmMap(String source, String dest) {
            this.source = source;
            this.dest = dest;
            this.originalRanges = new ArrayList<>();
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

    public static void main(String[] args) {
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
        final Map<String, FarmMap> almanacBySource = new HashMap<>();
        final Map<String, FarmMap> almanacByDest = new HashMap<>();
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
                if (currentEntry.isEmpty() || !almanacBySource.containsKey(currentEntry)) {
                    System.out.println("Uh-oh");
                    break;
                }
                String[] values = line.split(" ");
                Range range =
                        new Range(
                                Long.parseLong(values[0]),
                                Long.parseLong(values[1]),
                                Long.parseLong(values[2]));
                almanacBySource.get(currentEntry).ranges.add(range);
                almanacBySource.get(currentEntry).originalRanges.add(range);

            } else {
                String[] items = line.split("-|\\s+");
                currentEntry = items[0];
                FarmMap farmMap = new FarmMap(items[0], items[2]);
                almanacBySource.put(items[0], farmMap);
                almanacByDest.put(items[2], farmMap);
            }
        }

        //  Part 1
                Long closestLocation = Long.MAX_VALUE;
                for (Long seed : seeds) {
                    Long newLocation = convertSeedToLocation(almanacBySource, seed, "seed");
                    if (newLocation < closestLocation) {
                        closestLocation = newLocation;
                    }
                }
                System.out.println("Part 1: Closest location is " + closestLocation);

        //  Part 2

        //  Create a new map to work with seeds
        FarmMap seedMap = new FarmMap("elf", "seed");
        for (int i = 0; i < seeds.size(); i+=2) {
            Range range = new Range(seeds.get(i), seeds.get(i), seeds.get(i+1));
            seedMap.ranges.add(range);
            seedMap.originalRanges.add(range);
        }

        almanacBySource.put("elf", seedMap);
        almanacByDest.put("seed", seedMap);

        //  Fill in the range gaps in all the maps
        long highestMappedValue = 0;
        for (Map.Entry<String, FarmMap> map : almanacByDest.entrySet()) {

            for (Range range : map.getValue().ranges) {
                if (highestMappedValue < range.destEnd) {
                    highestMappedValue = range.destEnd;
                }
                if (highestMappedValue < range.sourceEnd) {
                    highestMappedValue = range.sourceEnd;
                }
            }
        }

        System.out.println("Highest mapped value: " + highestMappedValue);
        for (Map.Entry<String, FarmMap> map : almanacByDest.entrySet()) {
            fillInRangeGaps(map.getValue(), highestMappedValue);
            sortRangesBySource(map.getValue());
            int jennifer = 9;
        }


        //  These don't need to be split, so go ahead and add all the location's ranges to its split ranges
        almanacByDest.get("location").splitRanges.addAll(almanacByDest.get("location").ranges);
        String resourceConnection = "humidity";

        //  split all the ranges in the maps
        do {
            FarmMap lowerMap = almanacBySource.get(resourceConnection);
            FarmMap upperMap = almanacByDest.get(resourceConnection);
            List<Range> lowerRangesToCompare = lowerMap.splitRanges;
            List<Range> rangesToSplit = upperMap.ranges;
            List<Range> newUpperRanges = new ArrayList<>();
            Set<Range> tempRangeSet = new HashSet<>();
            for (Range upperRange : rangesToSplit) {
                splitUpperRangeAgainstAllLowerRanges(upperRange, lowerRangesToCompare,
                                                     newUpperRanges, tempRangeSet);
            }
            almanacByDest.get(resourceConnection).splitRanges.addAll(newUpperRanges);

            sortRangesBySource(lowerMap); // helpful for debugging
            sortRangesByDestination(upperMap); // helpful for debugging
            resourceConnection = upperMap.source;
            System.out.println();
        } while (!resourceConnection.equals("elf"));


        long lowestComplicatedSeedLocation = Long.MAX_VALUE;
        for(Range range : seedMap.splitRanges) {
            long beginningSeed = range.sourceStart;
            if (sourceIsWithinOriginalRanges(beginningSeed, seedMap)) {
                Long location = convertSeedToLocation(almanacBySource, beginningSeed, "seed");
                System.out.println("Seed: " + beginningSeed + " maps to " + location );
                if (location < lowestComplicatedSeedLocation) {
                    lowestComplicatedSeedLocation = location;
                }
            }
        }
        System.out.println("Lowest seed in this incredibly complicated problem is: " + lowestComplicatedSeedLocation);
//        List<Range> newUpperRanges = new ArrayList<>();
//        Set<Range> tempRangeSet = new HashSet<>();
//        Range upper = almanacBySource.get("apples").ranges.get(0);
//        List<Range> lowerRanges = almanacBySource.get("oranges").ranges;
//        splitUpperRangeAgainstAllLowerRanges(upper, lowerRanges, newUpperRanges, tempRangeSet);

        int jennifer = 9;
    }

    static boolean sourceIsWithinOriginalRanges(final long source, final FarmMap map) {
        for (Range originalRange : map.originalRanges) {
            if (originalRange.sourceIsWithinRange(source)) {
                return true;
            }
        }
        return false;
    }

    static Long convertSeedToLocation(
            final Map<String, FarmMap> almanac, Long value, String entry) {
        FarmMap currentMap = almanac.get(entry);
        if (entry.equals("location")) {
            return value;
        }

        for (Range range : currentMap.ranges) {
            if (value >= range.sourceStart && value <= range.sourceEnd) {
                return convertSeedToLocation(
                        almanac, range.destStart + value - range.sourceStart, currentMap.dest);
            }
        }

        return (convertSeedToLocation(almanac, value, currentMap.dest));
    }

    static void sortRangesByDestination(FarmMap map) {
        Collections.sort(map.splitRanges, Comparator.comparing(range -> range.destStart));
        Collections.sort(map.ranges, Comparator.comparing(range -> range.destStart));
    }

    static void sortRangesBySource(FarmMap map) {
        Collections.sort(map.splitRanges, Comparator.comparing(range -> range.sourceStart));
        Collections.sort(map.ranges, Comparator.comparing(range -> range.sourceStart));
    }

    static void fillInRangeGaps(FarmMap map, long highestValue) {
        sortRangesBySource(map);
        long currentValue = 0;
        int rangeIdx = 0;
        List<Range> newRanges = new ArrayList<>();
        while (currentValue < highestValue) {
            if (map.ranges.size() < rangeIdx + 1 && currentValue < highestValue) {
                Range endingRange = new Range(currentValue, currentValue, highestValue - currentValue + 1);
                newRanges.add(endingRange);
                break;
            }

            Range lowestRange = map.ranges.get(rangeIdx);
            if (currentValue < lowestRange.sourceStart) {
                long length = lowestRange.sourceStart - currentValue;
                Range gapRange = new Range(currentValue, currentValue, length);
                newRanges.add(gapRange);
                currentValue = lowestRange.sourceEnd + 1;
                rangeIdx++;
            } else if (currentValue == lowestRange.sourceStart) {
                currentValue = lowestRange.sourceEnd + 1;
                rangeIdx++;
            } else {
                System.out.println("Problem filling in the gaps" + map);
            }
        }
        map.ranges.addAll(newRanges);
    }

    static void splitUpperRangeAgainstAllLowerRanges(Range upperRange, List<Range> lowerRanges, List<Range> newUpperRanges, Set<Range> rangeSet) {
        boolean didSplit = false;
        for (Range lowerRange : lowerRanges) {
            List<Range> ranges = splitSingleUpperRangeAgainstSingleLowerRange(upperRange, lowerRange);
            if (ranges != null) {
                didSplit = true;
                for (Range range : ranges) {
                    splitUpperRangeAgainstAllLowerRanges(range, lowerRanges, newUpperRanges, rangeSet);
                }
            }
        }
        if (!didSplit && !rangeSet.contains(upperRange)) {
            newUpperRanges.add(upperRange);
            rangeSet.add(upperRange);
        }
    }

    static List<Range> splitSingleUpperRangeAgainstSingleLowerRange(Range upperRange, Range lowerRange) {
//        System.out.println("Comparing two ranges:");
//        System.out.println("Upper: " + upperRange);
//        System.out.println("Lower: " + lowerRange);

        if (upperRange.destEnd < lowerRange.sourceStart) {
//            System.out.println("Upper range below lower range");
            return null;
        }

        if (upperRange.destStart > lowerRange.sourceEnd) {
//            System.out.println("Upper range above lower range");
            return null;
        }

        //  upper dest range fully inside lower source range
        //  no need to split
        if (upperRange.destStart >= lowerRange.sourceStart
                && upperRange.destEnd <= lowerRange.sourceEnd) {
//            System.out.println("Upper range fully within lower range");
            return null;
        }

        //  upper dest range starts below within lower source range but ends within it
        if (upperRange.destStart < lowerRange.sourceStart
                && upperRange.destEnd >= lowerRange.sourceStart
                && upperRange.destEnd <= lowerRange.sourceEnd) {
            final long newLength = lowerRange.sourceStart - upperRange.destStart;
            final Range first = new Range(upperRange.destStart, upperRange.sourceStart, newLength);
            final Range second =
                    new Range(
                            upperRange.destStart + newLength,
                            upperRange.sourceStart + newLength,
                            upperRange.length - newLength);
            List<Range> splitRanges = new ArrayList<>();
            splitRanges.add(first);
            splitRanges.add(second);
//            System.out.println("Upper starts below, ends within");
//            System.out.println("First split: " + first);
//            System.out.println("Second split: " + second);
            return splitRanges;
        }

        //  upper dest range starts within lower source range but ends beyond it
        //  need to split
        if (upperRange.destStart >= lowerRange.sourceStart
                && upperRange.destStart <= lowerRange.sourceEnd
                && upperRange.destEnd > lowerRange.sourceEnd) {
//            System.out.println("Upper starts within, ends above");
            final long newLength = lowerRange.sourceEnd - upperRange.destStart + 1;
            Range first = new Range(upperRange.destStart, upperRange.sourceStart, newLength);
            Range second =
                    new Range(
                            upperRange.destStart + newLength,
                            upperRange.sourceStart + newLength,
                            upperRange.length - newLength);
//            System.out.println("First split: " + first);
//            System.out.println("Second split: " + second);
            return Arrays.asList(first, second);
        }

        //  upper dest range starts below and ends above lower source
        if (upperRange.destStart < lowerRange.sourceStart
                && upperRange.destEnd > lowerRange.sourceEnd) {
//            System.out.println("Upper range fully encompasses lower range");
            final long length1 = lowerRange.sourceStart - upperRange.destStart;
            final long length2 = lowerRange.sourceEnd - lowerRange.sourceStart + 1;
            final long length3 = upperRange.destEnd - lowerRange.sourceEnd;
            Range first = new Range(upperRange.destStart, upperRange.sourceStart, length1);
            Range second =
                    new Range(lowerRange.sourceStart, upperRange.sourceStart + length1, length2);
            Range third =
                    new Range(
                            lowerRange.sourceEnd + 1,
                            upperRange.sourceStart + length1 + length2,
                            length3);
//            System.out.println("First split: " + first);
//            System.out.println("Second split: " + second);
//            System.out.println("Third split: " + third);
            return Arrays.asList(first, second, third);
        }

        //  upper dest range
        System.out.println("Something isn't right");
        return null;
    }
}
