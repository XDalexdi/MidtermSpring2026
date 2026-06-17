import java.util.ArrayList;
import java.util.Scanner;

public class GameEngine {
    private GameState state;
    private boolean quiet;
    private Scanner scanner;

    public GameEngine(GameState state, boolean quiet, Scanner scanner) {
        this.state = state;
        this.quiet = quiet;
        this.scanner = scanner;
    }

    public boolean runGame() {
        state.prepareNewGame();
        int guard = 0;

        while (guard < 3000) {
            guard++;
            String name = state.playerNames.get(state.currentPlayer);
            ArrayList<String> hand = state.hands.get(state.currentPlayer);

            if (!quiet) {
                System.out.println("\nUp card: " + state.upCard + (state.calledColor.equals("") ? "" : " called " + state.calledColor));
                System.out.println(name + " hand: " + join(hand));
            }

            int chosen = -1;
            boolean isHuman = state.humanPlayers.get(state.currentPlayer);

            if (isHuman) {
                chosen = askHuman(hand);
            } else {
                chosen = BotStrategy.chooseCard(hand, state.upCard, state.calledColor);
            }

            if (chosen == -1) {
                String drawn = state.drawCard();
                hand.add(drawn);
                if (!quiet) System.out.println(name + " draws " + drawn);

                if (UnoRules.isLegal(drawn, state.upCard, state.calledColor)) {
                    if (!isHuman) {
                        chosen = hand.size() - 1;
                    } else {
                        System.out.print("Play drawn card " + drawn + "? y/n: ");
                        String answer = scanner.nextLine();
                        if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) chosen = hand.size() - 1;
                    }
                }
            }

            if (chosen >= 0) {
                if (chosen >= hand.size()) {
                    if (!quiet) System.out.println(name + " selected an invalid index and draws a penalty card.");
                    hand.add(state.drawCard());
                    state.nextPlayer();
                    continue;
                }

                String card = hand.get(chosen);
                boolean ok = UnoRules.isLegal(card, state.upCard, state.calledColor);
                if (!ok) {
                    if (!quiet) System.out.println(name + " tried illegal card " + card + " and draws a penalty card.");
                    hand.add(state.drawCard());
                    state.nextPlayer();
                    continue;
                }

                hand.remove(chosen);
                state.discard.add(state.upCard);
                state.upCard = card;
                state.calledColor = "";
                if (!quiet) System.out.println(name + " plays " + card);

                if (card.equals("W") || card.equals("W4")) {
                    if (isHuman) state.calledColor = askColor();
                    else state.calledColor = BotStrategy.chooseColor(hand);
                    if (!quiet) System.out.println(name + " calls " + state.calledColor);
                }

                if (hand.size() == 1 && !quiet) System.out.println(name + " says UNO!");

                if (hand.isEmpty()) {
                    int points = state.calculatePoints(state.currentPlayer);
                    if (!quiet) System.out.println(name + " wins and scores " + points);
                    return true; 
                }

                String cardRank = UnoRules.rank(card);
                if (cardRank.equals("DRAW_TWO") && !quiet) {
                    System.out.println(state.playerNames.get(getNextPlayerIndex(1)) + " draws two.");
                } else if (cardRank.equals("WILD_DRAW_FOUR") && !quiet) {
                    System.out.println(state.playerNames.get(getNextPlayerIndex(1)) + " draws four.");
                }
                
                state.applyAction(cardRank);
            } else {
                state.nextPlayer();
            }
        }
        if (!quiet) System.out.println("Game stopped at safety limit.");
        return false;
    }

    private int getNextPlayerIndex(int offset) {
        int idx = state.currentPlayer + (state.direction * offset);
        while (idx >= state.playerNames.size()) idx -= state.playerNames.size();
        while (idx < 0) idx += state.playerNames.size();
        return idx;
    }

    private int askHuman(ArrayList<String> hand) {
        while (true) {
            System.out.print("Choose card index/code or draw: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("DRAW")) return -1;
            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) return index;
            } catch (Exception ignored) {}
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).equals(input)) {
                    if (UnoRules.isLegal(hand.get(i), state.upCard, state.calledColor)) return i;
                    System.out.println("That card is not legal.");
                }
            }
            System.out.println("Card not found.");
        }
    }

    private String askColor() {
        while (true) {
            System.out.print("Call color R/Y/G/B: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("R") || input.equals("Y") || input.equals("G") || input.equals("B")) return input;
            System.out.println("Bad color.");
        }
    }

    private String join(ArrayList<String> cards) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            out.append(i).append(":").append(cards.get(i));
            if (i < cards.size() - 1) out.append(" ");
        }
        return out.toString();
    }
}
