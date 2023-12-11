package aoc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day07 {
    public static final boolean IS_PART_TWO = true;

    public enum HandType {
        FIVE_OF_A_KIND(6),
        FOUR_OF_A_KIND(5),
        FULL_HOUSE(4),
        THREE_OF_A_KIND(3),
        TWO_PAIR(2),
        ONE_PAIR(1),
        HIGH_CARD(0);

        private final int value;

        private HandType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static final Map<Character, Integer> cardTypes = Map.ofEntries(
        Map.entry('A', 14),
        Map.entry('K', 13),
        Map.entry('Q', 12),
        Map.entry('J', 11),
        Map.entry('T', 10),
        Map.entry('9', 9),
        Map.entry('8', 8),
        Map.entry('7', 7),
        Map.entry('6', 6),
        Map.entry('5', 5),
        Map.entry('4', 4),
        Map.entry('3', 3),
        Map.entry('2', 2)
    );

    public static class Hand implements Comparable<Hand> {
        final String cards;
        final long bid;
        final HandType type;

        long rank;

        Hand(String cards, long bid) {
            this.cards = cards;
            this.bid = bid;
            this.type = getHandType(cards);
        }

        public boolean doesBeat(Hand other) {
            if (this.type.getValue() > other.type.getValue()) {
                return true;
            }
            if (this.type.getValue() < other.type.getValue()) {
                return false;
            }
            //  if they're equal
            for (int i = 0; i < 5; i++) {
                char thisCard = this.cards.charAt(i);
                char thatCard = other.cards.charAt(i);
                if (thisCard == thatCard) {
                    continue;
                }
                int thisCardValue = cardTypes.get(thisCard);
                int thatCardValue = cardTypes.get(thatCard);
                if (IS_PART_TWO) {
                    if (thisCard == 'J') {
                        thisCardValue = 1;
                    }
                    if (thatCard == 'J') {
                        thatCardValue = 1;
                    }
                }
                if (thisCardValue > thatCardValue) {
                    return true;
                }
                return false;
            }
            System.out.println("Something went wrong");
            return false;
        }

        @Override
        public String toString() {
            return cards + " " + this.type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Hand that = (Hand) o;
            return this.cards.equals(that.cards);
        }

        @Override
        public int compareTo(Hand otherHand) {
            if (this.equals(otherHand)) {
                return 0;
            }
            return this.doesBeat(otherHand) ? 1 : -1;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.cards, this.bid);
        }
    }

    public static void main(String[] args){
        List<String> data = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File("data/day07.txt"))) {
            while (scanner.hasNext()) {
                data.add(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        List<Hand> hands = new ArrayList<>();
        for (String line : data) {
            String[] lineData = line.split(" ");
            Hand hand = new Hand(lineData[0], Long.parseLong(lineData[1]));
            hands.add(hand);
        }

        //  Part 1
        hands.sort(Hand::compareTo);
        long totalWinnings = 0L;
        for (int i = 0; i < hands.size(); i++) {
            hands.get(i).rank = i + 1;
            totalWinnings += hands.get(i).bid * (i + 1);
        }
        System.out.println("Part 1: Total winnings: " + totalWinnings);

        int jennifer = 9;
    }

    public static HandType getHandType(String cards) {
        Map<Character, Integer> sortedCards = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            if (sortedCards.containsKey(cards.charAt(i))) {
                sortedCards.put(cards.charAt(i), sortedCards.get(cards.charAt(i)) + 1);
            } else {
                sortedCards.put(cards.charAt(i), 1);
            }
        }

        List<Map.Entry<Character, Integer>> sortedCardsByFrequency = sortedCards.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());

        if (IS_PART_TWO) {
            //  if we have jacks, treat them as the highest-frequency card to maximize hand value
            if (sortedCards.containsKey('J')) {
                if (sortedCardsByFrequency.get(0).getKey() == 'J') {
                    if (sortedCardsByFrequency.size() > 1) {
                        sortedCardsByFrequency.get(1).setValue(sortedCardsByFrequency.get(1).getValue() + sortedCardsByFrequency.get(0).getValue());
                        sortedCardsByFrequency.remove(0);
                    }
                } else {
                    sortedCardsByFrequency.get(0).setValue(sortedCardsByFrequency.get(0).getValue() + sortedCards.get('J'));
                    int jIdx = 1;
                    for (int i = 1; i < sortedCardsByFrequency.size(); i++) {
                        if (sortedCardsByFrequency.get(i).getKey() == 'J') {
                            jIdx = i;
                            break;
                        }
                    }
                    sortedCardsByFrequency.remove(jIdx);
                }
            }
        }

        Entry<Character, Integer> mostFrequentCard = sortedCardsByFrequency.get(0);
        if (mostFrequentCard.getValue().equals(5)) {
            return HandType.FIVE_OF_A_KIND;
        }
        if (mostFrequentCard.getValue().equals(4)) {
            return HandType.FOUR_OF_A_KIND;
        }
        if (mostFrequentCard.getValue().equals(3)) {
            if (sortedCardsByFrequency.get(1).getValue().equals(2)) {
                return HandType.FULL_HOUSE;
            }
            return HandType.THREE_OF_A_KIND;
        }
        if (mostFrequentCard.getValue().equals(2)) {
            Entry<Character, Integer> secondCardGroup = sortedCardsByFrequency.get(1);
            if (sortedCardsByFrequency.get(1).getValue().equals(2)) {
                return HandType.TWO_PAIR;
            }
            return HandType.ONE_PAIR;
        }
        return HandType.HIGH_CARD;
    }
}
