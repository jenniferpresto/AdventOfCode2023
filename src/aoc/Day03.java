package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 {

    public static class EnginePart {
        String value;
        int start;
        int end;
        int row;
        boolean isPartNumber = false;

        EnginePart(final String value, final int start, final int row) {
            this.value = value;
            this.start = start;
            this.end = start + value.length() - 1;
            this.row = row;
        }

        boolean isAdjacent(EnginePart other) {
            if (this.row > other.row + 1 || this.row < other.row - 1) {
                return false;
            }
            if (this.start > other.end + 1 || this.end < other.start - 1) {
                return false;
            }
            return true;
        }
    }
    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("data/day03.txt"))) {
            while (scanner.hasNext()) {
                data.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        final List<EnginePart> numberParts = new ArrayList<>();
        final List<EnginePart> symbols = new ArrayList<>();
        final Pattern pAll = Pattern.compile("(\\d+)|([^.])");
        final Pattern pNum = Pattern.compile("\\d+");

        //  Split the engine into its number and symbol parts
        for (int i = 0; i < data.size(); i++) {
            final Matcher m = pAll.matcher(data.get(i));
            while (m.find()) {
                EnginePart part = new EnginePart(m.group(), m.start(), i);
                System.out.println(m.group() + " starts at " + m.start());
                if (pNum.matcher(m.group()).matches()) {
                    numberParts.add(part);
                } else {
                    symbols.add(part);
                }
            }
        }


        //  Part 1: Check the number parts against all the symbols
        int sum = 0;
        for (EnginePart numberPart : numberParts) {
            for (EnginePart symbol : symbols) {
                if (numberPart.isAdjacent(symbol)) {
                    sum += Integer.parseInt(numberPart.value);
                    break;
                }
            }
        }

        System.out.println("Part 1: Sum of all part numbers: " + sum);
    }
}
