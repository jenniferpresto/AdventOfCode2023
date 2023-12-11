import java.io.File;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day08 {
    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("data/day08.txt"))) {
            while (scanner.hasNext()) {
                data.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        String directions = data.get(0);
        Map<String, Map.Entry<String, String>> nodes = new HashMap<>();
        for (int i = 2; i < data.size(); i++) {
            String[] nodesOnly = data.get(i).split("(\\s=\\s\\()|(,\\s)|(\\))");
            Map.Entry<String, String> destNodes =
                    new SimpleImmutableEntry<>(nodesOnly[1], nodesOnly[2]);
            nodes.put(nodesOnly[0], destNodes);
        }

        //  Part 1
        String currentNode = "AAA";
        int idx = 0;
        long numSteps = 0L;
        while(true) {
            numSteps++;
            char direction = directions.charAt(idx);
            if(direction == 'L') {
                currentNode = nodes.get(currentNode).getKey();
            } else {
                currentNode = nodes.get(currentNode).getValue();
            }
            if (currentNode.equals("ZZZ")) {
                break;
            }
            idx = (idx + 1) % directions.length();
        }
        System.out.println("Num steps: " + numSteps);

        //  Part 2
        List<String> aNodes = new ArrayList<>();
        List<Long> aNodeSteps = new ArrayList<>();
        for (Map.Entry<String, Map.Entry<String, String>> node : nodes.entrySet()) {
            if (node.getKey().endsWith("A")) {
                aNodes.add(node.getKey());
            }
        }

        //  Get num steps for each A node
        for (int i = 0; i < aNodes.size(); i++) {
            currentNode = aNodes.get(i);
            numSteps = 0L;
            idx = 0;
            while(true) {
                numSteps++;
                char direction = directions.charAt(idx);
                if (direction == 'L') {
                    currentNode = nodes.get(currentNode).getKey();
                } else {
                    currentNode = nodes.get(currentNode).getValue();
                }
                if (currentNode.endsWith("Z")) {
                    break;
                }
                idx = (idx + 1) % directions.length();
            }
            aNodeSteps.add(numSteps);
            System.out.println("Num steps for node " + i + ", " + aNodes.get(i) + ": " + numSteps);
        }

        //  Get least common multiple of all those
        long lcm = aNodeSteps.get(0);
        for (int i = 1; i < aNodeSteps.size(); i++) {
            lcm = getLeastCommonMultiple(lcm, aNodeSteps.get(i));
        }

        System.out.println("Part 2: Smallest number of steps: " + lcm);
    }

    static long getLeastCommonMultiple(long a, long b) {
        long higher = Math.max(a, b);
        long lower = Math.min(a, b);
        long lcm = higher;
        while (lcm % lower != 0) {
            lcm += higher;
        }
        return lcm;
    }
}
