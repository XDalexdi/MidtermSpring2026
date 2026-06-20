import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameState {
    public ArrayList<String> playerNames = new ArrayList<>();
    public ArrayList<Boolean> humanPlayers = new ArrayList<>();
    public ArrayList<ArrayList<String>> hands = new ArrayList<>();
    public ArrayList<String> deck = new ArrayList<>();
    public ArrayList<String> discard = new ArrayList<>();
    public int[] scores = new int[10];
    public int currentPlayer = 0;
    public int direction = 1;
    public String upCard = "";
    public String calledColor = "";
    public Random random;

    public GameState(long seed) {
        this.random = new Random(seed);
    }

    public void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();
        if (human) {
            playerNames.add("You");
            humanPlayers.add(Boolean.TRUE);
            hands.add(new ArrayList<>());
        }
        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(Boolean.FALSE);
            hands.add(new ArrayList<>());
        }
    }

    public void prepareNewGame() {
        deck.clear();
        String[] colors = {"R", "Y", "G", "B"};
        for (String c : colors) {
            deck.add(c + "0");
            for (int n = 1; n <= 9; n++) {
                deck.add(c + n);
                deck.add(c + n);
            }
            deck.add(c + "S");
            deck.add(c + "S");
            deck.add(c + "R");
            deck.add(c + "R");
            deck.add(c + "+2");
            deck.add(c + "+2");
        }
        for (int i = 0; i < 4; i++) {
            deck.add("W");
            deck.add("W4");
        }
        Collections.shuffle(deck, random);
        discard.clear();
        for (ArrayList<String> hand : hands) hand.clear();

        for (ArrayList<String> hand : hands) {
            for (int j = 0; j < 7; j++) hand.add(drawCard());
        }

        upCard = drawCard();
        while (upCard.startsWith("W")) {
            discard.add(upCard);
            upCard = drawCard();
        }
        calledColor = "";
        direction = 1;
        currentPlayer = random.nextInt(playerNames.size());
    }

    public String drawCard() {
        if (deck.isEmpty()) {
            deck.addAll(discard);
            discard.clear();
            Collections.shuffle(deck, random);
        }
        if (deck.isEmpty()) return "W";
        return deck.remove(0);
    }

    public void nextPlayer() {
        currentPlayer += direction;
        if (currentPlayer >= playerNames.size()) currentPlayer = 0;
        if (currentPlayer < 0) currentPlayer = playerNames.size() - 1;
    }

    public void applyAction(String cardRank) {
        if (cardRank.equals("SKIP")) {
            nextPlayer();
            nextPlayer();
        } else if (cardRank.equals("REVERSE")) {
            direction *= -1;
            if (playerNames.size() == 2) {
                nextPlayer();
                nextPlayer();
            } else {
                nextPlayer();
            }
        } else if (cardRank.equals("DRAW_TWO")) {
            nextPlayer();
            hands.get(currentPlayer).add(drawCard());
            hands.get(currentPlayer).add(drawCard());
            nextPlayer();
        } else if (cardRank.equals("WILD_DRAW_FOUR")) {
            nextPlayer();
            for (int i = 0; i < 4; i++) hands.get(currentPlayer).add(drawCard());
            nextPlayer();
        } else {
            nextPlayer();
        }
    }

    public int calculatePoints(int winnerIndex) {
        int points = 0;
        for (int i = 0; i < hands.size(); i++) {
            if (i != winnerIndex) {
                for (String card : hands.get(i)) points += UnoRules.points(card);
            }
        }
        scores[winnerIndex] += points;
        return points;
    }
}