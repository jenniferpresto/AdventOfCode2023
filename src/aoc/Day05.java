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
            return("D: " + this.destStart + ".." + this.destEnd
                    + " S: " + this.sourceStart + ".." + this.sourceEnd
                    + " L: " + this.length);
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
                Range range = new Range(Long.parseLong(values[0]), Long.parseLong(values[1]), Long.parseLong(values[2]));
                almanacBySource.get(currentEntry).ranges.add(range);

            } else {
                String[] items = line.split("-|\\s+");
                currentEntry = items[0];
                FarmMap farmMap = new FarmMap(items[0], items[2]);
                almanacBySource.put(items[0], farmMap);
                almanacByDest.put(items[2], farmMap);
            }
        }

        //  Part 1
//        Long closestLocation = Long.MAX_VALUE;
//        for (Long seed : seeds) {
//            Long newLocation = convertSeedToLocation(almanacBySource, seed, "seed");
//            if (newLocation < closestLocation) {
//                closestLocation = newLocation;
//            }
//        }
//        System.out.println("Part 1: Closest location is " + closestLocation);

        //  Part 2

        almanacByDest.get("location").splitRanges.addAll(almanacByDest.get("location").ranges);
        sortSplitRangesBySource(almanacByDest.get("location"));
        boolean hasFinished = false;
        String resourceConnection = "humidity";
//        while (true) {
//            List<Range> lowerRangesToCompare = almanacBySource.get(resourceConnection).splitRanges;
//            List<Range> newlySplitRanges = splitUpperRanges(almanacByDest.get(resourceConnection).ranges, lowerRangesToCompare);
//            almanacByDest.get(resourceConnection).splitRanges.addAll(newlySplitRanges);
//            sortSplitRangesBySource(almanacByDest.get(resourceConnection));
//            if(resourceConnection.equals("soil")) {
//                break;
//            }
//            resourceConnection = almanacByDest.get(resourceConnection).source;
//        }
        Range upper = almanacBySource.get("temperature").ranges.get(1);
        Range lower = almanacBySource.get("humidity").ranges.get(0);
        splitUpperRangeToLowerRange(upper, lower);
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

    static List<Range> splitUpperRangeToLowerRange(Range upperRange, Range lowerRange) {
        System.out.println("Comparing two ranges:");
        System.out.println("Upper: " + upperRange);
        System.out.println("Lower: " + lowerRange);
        //  we've sorted the ranges
        //  we can skip ranges until we get high enough
        if (upperRange.destEnd < lowerRange.sourceStart) {
            System.out.println("Upper range below lower range");
            return null;
        }

        if (upperRange.destStart > lowerRange.sourceEnd) {
            System.out.println("Upper range above lower range");
            return null;
        }

        //  upper dest range fully inside lower source range
        //  no need to split
        if (upperRange.destStart >= lowerRange.sourceStart && upperRange.destEnd <= lowerRange.sourceEnd) {
            System.out.println("Upper range fully within lower range");
            return null;
        }

        //  upper dest range starts below within lower source range but ends within it
        if (upperRange.destStart < lowerRange.sourceStart && upperRange.destEnd >= lowerRange.sourceStart) {
            final long newLength = lowerRange.sourceStart - upperRange.destStart;
            final Range first = new Range(upperRange.destStart, upperRange.sourceStart, newLength);
            final Range second = new Range(upperRange.destStart + newLength, upperRange.sourceStart + newLength, upperRange.length - newLength);
            List<Range> splitRanges = new ArrayList<>();
            splitRanges.add(first);
            splitRanges.add(second);
            System.out.println("Upper starts below, ends within");
            System.out.println("First split: " + first);
            System.out.println("Second split: " + second);
            return splitRanges;
        }

        //  upper dest range starts within lower source range but ends beyond it
        //  need to split
        if (upperRange.destStart >= lowerRange.sourceStart && upperRange.destEnd > lowerRange.sourceEnd) {
            System.out.println("Upper starts within, ends above");
            final long newLength = lowerRange.sourceEnd - upperRange.destStart + 1;
            Range first = new Range(upperRange.destStart, upperRange.sourceStart, newLength);
            Range second = new Range(upperRange.destStart + newLength, upperRange.sourceStart + newLength, upperRange.length - newLength);
            List<Range> splitRanges = new ArrayList<>();
            splitRanges.add(first);
            splitRanges.add(second);
            System.out.println("First split: " + first);
            System.out.println("Second split: " + second);
            return splitRanges;
        }

        //  upper dest range starts below and ends above lower source
        if (upperRange.destStart < lowerRange.sourceStart && upperRange.destEnd > lowerRange.sourceEnd) {
            System.out.println("Upper range fully encompasses lower range");
            System.out.println("Doing nothing for now");
            return null;
        }

        //  upper dest range

        System.out.println("Leftover: upper: " + upperRange + ", lower: " +  lowerRange);
        return Collections.singletonList(upperRange);

    }
    static List<Range> splitRange(Range upperRange, List<Range> lowerRanges) {
//        for (Range lowerRange : lowerRanges) {
//            System.out.println("Comparing two ranges:");
//            System.out.println("Upper: " + upperRange);
//            System.out.println("Lower: " + lowerRange);
//            //  we've sorted the ranges
//            //  we can skip ranges until we get high enough
//            if (upperRange.destEnd < lowerRange.sourceStart) {
//                System.out.println("Too low: " + lowerRange);
//                return Collections.singletonList(upperRange);
//            }
//
//            //  upper dest range fully inside lower source range
//            //  no need to split
//            if (upperRange.destStart >= lowerRange.sourceStart && upperRange.destEnd <= lowerRange.sourceEnd) {
//                return Collections.singletonList(upperRange);
//            }
//
//            //  upper dest range starts below within lower source range but ends within it
//            if (upperRange.destStart < lowerRange.sourceStart && upperRange.destEnd >= lowerRange.sourceStart) {
//                final long newLength = lowerRange.sourceStart - upperRange.destStart;
//                final Range first = new Range(upperRange.destStart, upperRange.sourceStart, newLength);
//                final Range second = new Range(lowerRange.sourceStart, upperRange.sourceStart + newLength, upperRange.length - newLength);
//                List<Range> splitRanges = new ArrayList<>();
//                splitRanges.addAll(splitRange(first, lowerRanges));
//                splitRanges.addAll(splitRange(second, lowerRanges));
//                System.out.println("Splitting : " + lowerRange);
//                return splitRanges;
//            }
//
//            //  upper dest range starts within lower source range but ends beyond it
//            //  need to split
//            if (upperRange.destStart >= lowerRange.sourceStart && upperRange.destEnd > lowerRange.sourceEnd) {
//                System.out.println("Now we have to do something: lower range: " + lowerRange);
//                System.out.println("Upper range: " + upperRange);
//                final long newLength = upperRange.destEnd - lowerRange.sourceEnd;
//                Range first = new Range(upperRange.destStart, upperRange.sourceStart, newLength);
//                Range second = new Range(lowerRange.sourceEnd + 1, upperRange.sourceStart + newLength, upperRange.length - newLength);
//                System.out.println("First split: " + first);
//                System.out.println("Second split: " + second);
//                List<Range> splitRanges = new ArrayList<>();
//                splitRanges.addAll(splitRange(first, lowerRanges));
//                splitRanges.addAll(splitRange(second, lowerRanges));
//                return splitRanges;
//            }
//
//            System.out.println("Leftover: upper: " + upperRange + ", lower: " +  lowerRange);
//            return Collections.singletonList(upperRange);
//        }
        System.out.println("Something went wrong");
        return null;
    }

    static List<Range> splitUpperRanges(List<Range> upperRanges, List<Range> lowerRanges) {

        List<Range> splitRanges = new ArrayList<>();


        for (Range upperRange : upperRanges) {
            splitRanges.addAll(splitRange(upperRange, lowerRanges));
        }
        return splitRanges;
//            System.out.println("Upper range: " + upperRange);
//            boolean didSplitRange = false;
//            for (Range lowerRange : lowerRanges) {
//                //  we've sorted the ranges
//                //  we can skip ranges until we get high enough
//                if (upperRange.destEnd < lowerRange.sourceStart) {
//                    System.out.println("Too low: " + lowerRange);
//                    break;
//                }
//
//                //  upper dest range fully inside lower source range
//                //  no need to split
//                if (upperRange.destStart >= lowerRange.sourceStart && upperRange.destEnd <= lowerRange.sourceEnd) {
//                    newRanges.add(upperRange);
//                    System.out.println("Fits inside: " + lowerRanges);
//                    break;
//                }
//
//                //  upper dest range starts below within lower source range but ends within it
//                if (upperRange.destStart < lowerRange.sourceStart && upperRange.destEnd >= lowerRange.sourceStart) {
//                    final long newLength = lowerRange.sourceStart - upperRange.destStart;
//                    final Range first = new Range(upperRange.destStart, upperRange.sourceStart, newLength);
//                    final Range second = new Range(lowerRange.sourceStart, upperRange.sourceStart + newLength, upperRange.length - newLength);
//                    List<Range> splitRanges = new ArrayList<>();
//                    splitRanges.add(first);
//                    splitRanges.add(second);
//                    newRanges.addAll(splitUpperRanges(splitRanges, lowerRanges));
//                    System.out.println("Splitting: " + lowerRange);
//                    didSplitRange = true;
//                    break;
//                }
//
//                //  upper dest range starts within lower source range but ends beyond it
//                //  need to split
//                if (upperRange.destStart >= lowerRange.sourceStart && upperRange.destEnd > lowerRange.sourceEnd) {
//                    System.out.println("Now we have to do something: " + lowerRange);
//                    final long newLength = lowerRange.sourceEnd - upperRange.destEnd;
//                    Range first = new Range(upperRange.destStart, upperRange.sourceStart, newLength);
//                    Range second = new Range(lowerRange.sourceEnd + 1, upperRange.sourceStart + newLength, upperRange.length - newLength);
//                    List<Range> splitRanges = new ArrayList<>();
//                    splitRanges.add(first);
//                    splitRanges.add(second);
//                    newRanges.addAll(splitUpperRanges(splitRanges, lowerRanges));
//                    didSplitRange = true;
//                    break;
//                }
//            }
//
//            if (!didSplitRange) {
//                System.out.println("No match; keep as is");
//                newRanges.add(upperRange);
//            }
//        }
//        return newRanges;
    }
}
