package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        public boolean valueIsWithinSource(long value) {
            return value >= this.sourceStart && value <= this.sourceEnd;
        }

        public boolean valueIsWithinDestination(long value) {
            return value >= this.destStart && value <= this.destEnd;
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

        public String getName() {
            return this.source + "-to-" + this.dest;
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

//        FarmMap upperTestMap = almanacBySource.get("soil");
//        FarmMap lowerTestMap = almanacBySource.get("fertilizer");
//        lowerTestMap.splitRanges.addAll(lowerTestMap.ranges);
//        List<Range> newSplitRanges = combineRanges(upperTestMap, lowerTestMap);
//        upperTestMap.splitRanges.addAll(newSplitRanges);


        //  ******************************************
        //  These don't need to be split, so go ahead and add all the location's ranges to its split ranges
        almanacByDest.get("location").splitRanges.addAll(almanacByDest.get("location").ranges);
        String resourceConnection = "humidity";

        while(true) {
            FarmMap lowerMap = almanacBySource.get(resourceConnection);
            FarmMap upperMap = almanacByDest.get(resourceConnection);
            upperMap.splitRanges.addAll(combineRanges(upperMap, lowerMap));
            resourceConnection = upperMap.source;
            if (resourceConnection.equals("elf")) {
                break;
            }
        }

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
        System.out.println("Part 2: Lowest seed in this annoyingly complicated question is: " + lowestComplicatedSeedLocation);

    }

    static boolean sourceIsWithinOriginalRanges(final long source, final FarmMap map) {
        for (Range originalRange : map.ranges) {
            if (originalRange.valueIsWithinSource(source)) {
                return true;
            }
        }
//        System.out.println("Not in original source: " + source);
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

    static List<Range> combineRanges(FarmMap upper, FarmMap lower) {
        System.out.println("Combining ranges");
        System.out.println("Upper: " + upper.getName());
        System.out.println("Lower: " + lower.getName());

        sortRangesByDestination(upper);
        sortRangesBySource(lower);
        int upperIdx = 0;
        int lowerIdx = 0;
        long currentValue = 0L;
        List<Range> upperRanges = upper.ranges;
        List<Range> lowerRanges = lower.splitRanges;

        List<Range> newSplitRanges = new ArrayList<>();

        while(true) {
            if (upperRanges.size() - 1 < upperIdx && lowerRanges.size() - 1 < lowerIdx) {
                System.out.println("We finished both at once: upperIdx=" + upperIdx + ", lowerIdx=" + lowerIdx);
                break;
            }

            //  we have only lower ranges left
            if (upperRanges.size() - 1 < upperIdx) {
                //  add a range corresponding to lower range
                Range lowerRangeToDuplicate = lowerRanges.get(lowerIdx);

                //  include the remainer of the current lower range, if current value is within it
                if (lowerRangeToDuplicate.valueIsWithinSource(currentValue)) {
                    newSplitRanges.add(new Range(currentValue, currentValue, lowerRangeToDuplicate.sourceEnd - currentValue + 1));
                    lowerIdx++;
                }

                //  then add the rest
                for (int i = lowerIdx; i < lowerRanges.size(); i++) {
                    newSplitRanges.add(new Range(lowerRanges.get(i).sourceStart, lowerRanges.get(i).sourceStart, lowerRanges.get(i).length));
                }
                break;
            }

            //  we have only upper ranges left
            if (lowerRanges.size() - 1 < lowerIdx) {
                //  duplicate upper range
                Range upperRangeToDuplicate = upperRanges.get(upperIdx);


                //  include the remainer of the current upper range, if current value is within it
                if (upperRangeToDuplicate.valueIsWithinDestination(currentValue)) {

                    newSplitRanges.add(new Range(currentValue, currentValue - (upperRangeToDuplicate.destStart - upperRangeToDuplicate.sourceStart), upperRangeToDuplicate.destEnd - currentValue + 1));
                    upperIdx++;
                }

                //  then add the rest
                for (int i = upperIdx; i < upperRanges.size(); i++) {
                    newSplitRanges.add(upperRanges.get(i));
                }
                break;
            }

            Range currentUpperRange = upperRanges.get(upperIdx);
            Range currentLowerRange = lowerRanges.get(lowerIdx);

            if (!currentUpperRange.valueIsWithinDestination(currentValue) && !currentLowerRange.valueIsWithinSource(currentValue)) {
                long lowestNextRangeStart = Math.min(currentUpperRange.destStart, currentLowerRange.sourceStart);
                currentValue = lowestNextRangeStart;
            }

            long destSourceDiff = currentUpperRange.destStart - currentUpperRange.sourceStart;

            long nextLowestPoint;
            //  in upper range
            if (currentUpperRange.valueIsWithinDestination(currentValue)) {
                if (currentLowerRange.valueIsWithinSource(currentValue)) {
                    nextLowestPoint = Math.min(currentUpperRange.destEnd, currentLowerRange.sourceEnd);
                } else {
                    if(currentLowerRange.sourceEnd < currentValue) {
                        nextLowestPoint = currentUpperRange.destEnd;
                    } else {
                        nextLowestPoint = Math.min(currentUpperRange.destEnd, currentLowerRange.sourceStart);
                    }
                }

                if (nextLowestPoint == currentUpperRange.destEnd) {
                    newSplitRanges.add(new Range(currentValue, currentValue - destSourceDiff, currentUpperRange.destEnd - currentValue + 1));
                    upperIdx++;
                    if (nextLowestPoint == currentLowerRange.sourceEnd) {
                        lowerIdx++;
                    }
                    currentValue = currentUpperRange.destEnd + 1;
                } else if (nextLowestPoint == currentLowerRange.sourceStart) {
                    if (currentValue == currentLowerRange.sourceEnd) {
                        newSplitRanges.add(new Range(currentValue, currentValue - destSourceDiff, 1));
                        lowerIdx++;
                        currentValue++;
                    } else {
                        newSplitRanges.add(new Range(currentValue, currentValue - destSourceDiff, currentLowerRange.sourceStart - currentValue));
                        currentValue = currentLowerRange.sourceStart;
                    }
                } else if (nextLowestPoint == currentLowerRange.sourceEnd) {
                    newSplitRanges.add(new Range(currentValue, currentValue - destSourceDiff, currentLowerRange.sourceEnd - currentValue + 1));
                    lowerIdx++;
                    currentValue = currentLowerRange.sourceEnd + 1;
                } else {
                    System.out.println("Something went wrong, starting point within upper range");
                }
            }
            //  in lower range only
            else if (currentLowerRange.valueIsWithinSource(currentValue)) {
                if (currentUpperRange.destEnd < currentValue) {
                    nextLowestPoint = currentLowerRange.sourceEnd;
                } else {
                    nextLowestPoint = Math.min(currentUpperRange.destStart, currentLowerRange.sourceEnd);
                }
                if (nextLowestPoint == currentUpperRange.destStart) {
                    newSplitRanges.add(new Range(currentValue, currentValue, currentUpperRange.destStart - currentValue));
                    currentValue = currentUpperRange.destStart;
                } else if (nextLowestPoint == currentLowerRange.sourceEnd) {
                    newSplitRanges.add(new Range(currentValue, currentValue, 1));
                    currentValue++;
                    lowerIdx++;
                } else if (nextLowestPoint == currentUpperRange.destEnd) {
                    System.out.println("Something is wrong: this shouldn't happen");
                } else {
                    System.out.println("Something went wrong, starting point within lower range");
                }
            } else {
                System.out.println("Something went wrong, value is in neither range");
            }
        }

        return newSplitRanges;
    }
}
