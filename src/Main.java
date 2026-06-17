import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static ArrayList<String> playerNames = new ArrayList<String>();
    static ArrayList<Boolean> humanPlayers = new ArrayList<Boolean>();
    static ArrayList<ArrayList<String>> hands = new ArrayList<ArrayList<String>>();
    static ArrayList<String> deck = new ArrayList<String>();
    static ArrayList<String> discard = new ArrayList<String>();
    static int[] scores = new int[10];
    static int currentPlayer = 0;
    static int direction = 1;
    static String upCard = "";
    static String calledColor = "";
    static boolean quiet = false;
    static Random random = new Random();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        long seed = System.currentTimeMillis();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N]");
                return;
            }
        }

        random = new Random(seed);
        setupPlayers(bots, human);

        if (playerNames.size() < 2 || playerNames.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        for (int g = 1; g <= games; g++) {
            if (!quiet) {
                System.out.println("\n=== Game " + g + " ===");
            }
            playGame();
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < playerNames.size(); i++) {
            System.out.println(playerNames.get(i) + ": " + scores[i]);
        }
    }

    static void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();
        if (human) {
            playerNames.add("You");
            humanPlayers.add(Boolean.TRUE);
            hands.add(new ArrayList<String>());
        }
        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(Boolean.FALSE);
            hands.add(new ArrayList<String>());
        }
    }

    static void playGame() {
        deck.clear();
        String[] colors = {"R", "Y", "G", "B"};
        for (int c = 0; c < colors.length; c++) {
            deck.add(colors[c] + "0");
            for (int n = 1; n <= 9; n++) {
                deck.add(colors[c] + n);
                deck.add(colors[c] + n);
            }
            deck.add(colors[c] + "S");
            deck.add(colors[c] + "S");
            deck.add(colors[c] + "R");
            deck.add(colors[c] + "R");
            deck.add(colors[c] + "+2");
            deck.add(colors[c] + "+2");
        }
        for (int i = 0; i < 4; i++) {
            deck.add("W");
            deck.add("W4");
        }
        Collections.shuffle(deck, random);
        discard.clear();
        for (int i = 0; i < hands.size(); i++) {
            hands.get(i).clear();
        }
        for (int i = 0; i < playerNames.size(); i++) {
            for (int j = 0; j < 7; j++) {
                hands.get(i).add(draw());
            }
        }
        upCard = draw();
        while (upCard.startsWith("W")) {
            discard.add(upCard);
            upCard = draw();
        }
        calledColor = "";
        direction = 1;
        currentPlayer = random.nextInt(playerNames.size());

        int guard = 0;
        while (guard < 3000) {
            guard++;
            String name = playerNames.get(currentPlayer);
            ArrayList<String> hand = hands.get(currentPlayer);

            if (!quiet) {
                System.out.println("\nUp card: " + upCard + (calledColor.equals("") ? "" : " called " + calledColor));
                System.out.println(name + " hand: " + join(hand));
            }

            int chosen = -1;
            if (humanPlayers.get(currentPlayer).booleanValue()) {
                chosen = askHuman(hand);
            } else {
                chosen = chooseBotCard(hand);
            }

            if (chosen == -1) {
                String drawn = draw();
                hand.add(drawn);
                if (!quiet) {
                    System.out.println(name + " draws " + drawn);
                }
                if (UnoRules.isLegal(drawn, upCard, calledColor)) {
                    if (!humanPlayers.get(currentPlayer).booleanValue()) {
                        chosen = hand.size() - 1;
                    } else {
                        System.out.print("Play drawn card " + drawn + "? y/n: ");
                        String answer = scanner.nextLine();
                        if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                            chosen = hand.size() - 1;
                        }
                    }
                }
            }

            if (chosen >= 0) {
                if (chosen >= hand.size()) {
                    if (!quiet) {
                        System.out.println(name + " selected an invalid index and draws a penalty card.");
                    }
                    hand.add(draw());
                    next();
                    continue;
                }

                String card = hand.get(chosen);
                boolean ok = UnoRules.isLegal(card, upCard, calledColor);
                if (!ok) {
                    if (!quiet) {
                        System.out.println(name + " tried illegal card " + card + " and draws a penalty card.");
                    }
                    hand.add(draw());
                    next();
                    continue;
                }

                hand.remove(chosen);
                discard.add(upCard);
                upCard = card;
                calledColor = "";
                if (!quiet) {
                    System.out.println(name + " plays " + card);
                }

                if (card.equals("W") || card.equals("W4")) {
                    if (humanPlayers.get(currentPlayer).booleanValue()) {
                        calledColor = askColor();
                    } else {
                        calledColor = chooseBotColor(hand);
                    }
                    if (!quiet) {
                        System.out.println(name + " calls " + calledColor);
                    }
                }

                if (hand.size() == 1 && !quiet) {
                    System.out.println(name + " says UNO!");
                }

                if (hand.size() == 0) {
                    processWin(currentPlayer);
                    return;
                }

                applyCardEffect(card);

            } else {
                next();
            }
        }
        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    static String draw() {
        if (deck.size() == 0) {
            deck.addAll(discard);
            discard.clear();
            Collections.shuffle(deck, random);
        }
        if (deck.size() == 0) {
            return "W";
        }
        return deck.remove(0);
    }

    static int chooseBotCard(ArrayList<String> hand) {
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (UnoRules.isLegal(card, upCard, calledColor) && UnoRules.rank(card).equals("DRAW_TWO")) return i;
        }
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (UnoRules.isLegal(card, upCard, calledColor) && UnoRules.rank(card).equals("SKIP")) return i;
        }
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (UnoRules.isLegal(card, upCard, calledColor) && UnoRules.rank(card).equals("NUMBER")) return i;
        }
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).startsWith("W")) return i;
        }
        return -1;
    }

    static int askHuman(ArrayList<String> hand) {
        while (true) {
            System.out.print("Choose card index/code or draw: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("DRAW")) {
                return -1;
            }
            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) {
                    return index;
                }
            } catch (Exception ignored) {
            }
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).equals(input)) {
                    if (UnoRules.isLegal(hand.get(i), upCard, calledColor)) {
                        return i;
                    }
                    System.out.println("That card is not legal.");
                }
            }
            System.out.println("Card not found.");
        }
    }

    static String askColor() {
        while (true) {
            System.out.print("Call color R/Y/G/B: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("R")) return "R";
            if (input.equals("Y")) return "Y";
            if (input.equals("G")) return "G";
            if (input.equals("B")) return "B";
            System.out.println("Bad color.");
        }
    }

    static String chooseBotColor(ArrayList<String> hand) {
        int r = 0, y = 0, g = 0, b = 0;
        for (int i = 0; i < hand.size(); i++) {
            String c = UnoRules.color(hand.get(i));
            if (c.equals("R")) r++;
            else if (c.equals("Y")) y++;
            else if (c.equals("G")) g++;
            else if (c.equals("B")) b++;
        }
        if (r >= y && r >= g && r >= b) return "R";
        else if (y >= r && y >= g && y >= b) return "Y";
        else if (g >= r && g >= y && g >= b) return "G";
        else return "B";
    }

    static void next() {
        currentPlayer += direction;
        if (currentPlayer >= playerNames.size()) {
            currentPlayer = 0;
        }
        if (currentPlayer < 0) {
            currentPlayer = playerNames.size() - 1;
        }
    }

    static String join(ArrayList<String> cards) {
        String out = "";
        for (int i = 0; i < cards.size(); i++) {
            out += i + ":" + cards.get(i);
            if (i < cards.size() - 1) {
                out += " ";
            }
        }
        return out;
    }

    static void processWin(int winnerIndex) {
        int points = 0;
        for (int i = 0; i < hands.size(); i++) {
            if (i != winnerIndex) {
                for (int j = 0; j < hands.get(i).size(); j++) {
                    points += UnoRules.points(hands.get(i).get(j));
                }
            }
        }
        scores[winnerIndex] += points;
        if (!quiet) {
            System.out.println(playerNames.get(winnerIndex) + " wins and scores " + points);
        }
    }

    static void applyCardEffect(String card) {
        String cardRank = UnoRules.rank(card);

        if (cardRank.equals("SKIP")) {
            next();
            next();
        } else if (cardRank.equals("REVERSE")) {
            direction = direction * -1;
            if (playerNames.size() == 2) {
                next();
                next();
            } else {
                next();
            }
        } else if (cardRank.equals("DRAW_TWO")) {
            next();
            hands.get(currentPlayer).add(draw());
            hands.get(currentPlayer).add(draw());
            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws two.");
            }
            next();
        } else if (cardRank.equals("WILD_DRAW_FOUR")) {
            next();
            for (int i = 0; i < 4; i++) {
                hands.get(currentPlayer).add(draw());
            }
            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws four.");
            }
            next();
        } else {
            next();
        }
    }

    static void selfTest() {
        int passed = 0;
        int total = 0;

        System.out.println("Running characterization tests...");

        total++; if (UnoRules.color("R5").equals("R")) passed++; else fail("color R5");
        total++; if (UnoRules.rank("G+2").equals("DRAW_TWO")) passed++; else fail("rank +2");
        total++; if (UnoRules.number("B9") == 9) passed++; else fail("number B9");
        total++; if (UnoRules.points("W4") == 50) passed++; else fail("wild points");
        total++; if (UnoRules.points("YS") == 20) passed++; else fail("skip points");
        total++; if (UnoRules.points("R7") == 7) passed++; else fail("number points");

        total++; if (UnoRules.isLegal("R2", "R9", "")) passed++; else fail("match by color");
        total++; if (UnoRules.isLegal("G9", "R9", "")) passed++; else fail("match by number");
        total++; if (UnoRules.isLegal("GS", "YS", "")) passed++; else fail("match by action (skip)");
        total++; if (UnoRules.isLegal("B+2", "R+2", "")) passed++; else fail("match by action (+2)");
        total++; if (!UnoRules.isLegal("B3", "R9", "")) passed++; else fail("illegal mismatch");

        total++; if (UnoRules.isLegal("W", "R9", "")) passed++; else fail("wild is always legal");
        total++; if (UnoRules.isLegal("W4", "B2", "")) passed++; else fail("wild4 is always legal");
        total++; if (UnoRules.isLegal("B3", "W", "B")) passed++; else fail("match called color");
        total++; if (!UnoRules.isLegal("G4", "W", "B")) passed++; else fail("mismatch called color");

        ArrayList<String> h1 = new ArrayList<String>();
        h1.add("B3"); h1.add("R4"); h1.add("W");
        upCard = "R9"; calledColor = "";
        total++; if (chooseBotCard(h1) == 1) passed++; else fail("bot plays normal before wild");

        ArrayList<String> h2 = new ArrayList<String>();
        h2.add("B1"); h2.add("B2"); h2.add("R3");
        total++; if (chooseBotColor(h2).equals("B")) passed++; else fail("bot color choice based on max count");

        ArrayList<String> h3 = new ArrayList<String>();
        h3.add("W"); h3.add("R5"); h3.add("RS"); h3.add("R+2");
        upCard = "R9"; calledColor = "";
        total++; if (chooseBotCard(h3) == 3) passed++; else fail("bot prioritizes draw two");

        ArrayList<String> h4 = new ArrayList<String>();
        h4.add("W"); h4.add("R5"); h4.add("RS");
        total++; if (chooseBotCard(h4) == 2) passed++; else fail("bot prioritizes skip");

        deck.clear();
        discard.clear();
        total++; if (draw().equals("W")) passed++; else fail("empty deck and discard yields W");

        System.out.println("Passed " + passed + " / " + total + " characterization checks.");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }
}
