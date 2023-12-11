import java.io.File;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
//        while(true) {
//            numSteps++;
//            char direction = directions.charAt(idx);
//            if(direction == 'L') {
//                currentNode = nodes.get(currentNode).getKey();
//            } else {
//                currentNode = nodes.get(currentNode).getValue();
//            }
//            if (currentNode.equals("ZZZ")) {
//                break;
//            }
//            idx = (idx + 1) % directions.length();
//        }
//        System.out.println("Num steps: " + numSteps);

        //  Part 2

        List<List<String>> allPaths = new ArrayList<>();
        List<String> lastNodeInPaths = new ArrayList<>();
        for (Map.Entry<String, Map.Entry<String, String>> node : nodes.entrySet()) {
            if (node.getKey().endsWith("A")) {
                List<String> path = new ArrayList<>();
                path.add(node.getKey());
                allPaths.add(path);
                lastNodeInPaths.add(node.getKey());
            }
        }

        numSteps = 0L;
        idx = 0;
        while(true) {
            numSteps++;
            boolean allZ = true;
            for (int i = 0; i < allPaths.size(); i++) {
                String lastNode = allPaths.get(i).getLast();
                String nextNode = "";
                char direction = directions.charAt(idx);
                if (direction == 'L') {
                    nextNode = nodes.get(lastNode).getKey();
                    allPaths.get(i).add(nextNode);
                } else {
                    nextNode = nodes.get(lastNode).getValue();
                    allPaths.get(i).add(nextNode);
                }
                if (!nextNode.endsWith("Z")) {
                    allZ = false;
                }
            }
            if (allZ) {
                break;
            }
            idx = (idx + 1) % directions.length();
        }
        System.out.println("Num steps: " + numSteps);
        int jennifer = 9;
    }
}
