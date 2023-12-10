package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day06 {
    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("data/day06.txt"))) {
            while (scanner.hasNext()) {
                data.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        List<Long> times = Arrays.stream(data.get(0).split("\\D+"))
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Long> records = Arrays.stream(data.get(1).split("\\D+"))
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
        long numWaysToWin[] = new long[times.size()];

        for (int i = 0; i < times.size(); i++) {
            boolean hasWon = false;
            System.out.println("Race duration: " + times.get(i));
            for (long j = 0L; j < times.get(i); j++) {
                long record = records.get(i);
                if (getDistance(j, times.get(i)) > record) {
                    numWaysToWin[i]++;
                    hasWon = true;
                } else {
                    if (hasWon) {
                        break;
                    }
                }
            }
        }

        long productOfWaysToWin = 1L;
        for(long ways : numWaysToWin) {
            productOfWaysToWin *= ways;
        }
        System.out.println("Part 1: " + productOfWaysToWin);

        //  Part 2
        long numWaysToWinSingle = 0L;
        //  test data
        //  long time = 71530;
        //  long record = 940200;

        //  real data
        long time = 53897698L;
        long record = 313109012141201L;
        boolean hasWon = false;

        for (long buttonDuration = 0L; buttonDuration < time; buttonDuration++) {
            if(getDistance(buttonDuration, time) > record) {
                numWaysToWinSingle++;
                hasWon = true;
            } else {
                if (hasWon) {
                    break;
                }
            }
        }

        System.out.println("Part 2: " + numWaysToWinSingle);

    }

    static long getDistance(long buttonDuration, long raceDuration) {
        return (raceDuration - buttonDuration) * buttonDuration;
    }
}
