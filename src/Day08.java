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
        String startingNode = "";
        Map<String, Map.Entry<String, String>> nodes = new HashMap<>();
        for (int i = 2; i < data.size(); i++) {
            String[] nodesOnly = data.get(i).split("(\\s=\\s\\()|(,\\s)|(\\))");
            Map.Entry<String, String> destNodes =
                    new SimpleImmutableEntry<>(nodesOnly[1], nodesOnly[2]);
            nodes.put(nodesOnly[0], destNodes);
        }

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
            if (currentNode.equals("BLT")) {
                System.out.println(idx + ": " + currentNode);
            }

            if (currentNode.equals("ZZZ")) {
                break;
            }
            idx = (idx + 1) % directions.length();
        }
        System.out.println("Num steps: " + numSteps);
    }
}
