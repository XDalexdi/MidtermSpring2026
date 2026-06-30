import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class GameEngine {
    private static final Logger logger = Logger.getLogger(GameEngine.class.getName());
    private boolean quiet;
    private Scanner scanner;

    // Core Game State mapped to our new Object-Oriented classes
    private UnoDeck deck;
    private TurnManager turnManager;
    private UnoCallManager callManager;
    private List<List<UnoCard>> playerHands;
    private List<String> playerNames;
    private List<Boolean> isHumanPlayer;
    private UnoCard topCard;
    private CardColor activeColor;

    public GameEngine(List<String> playerNames, List<Boolean> isHumanPlayer, boolean quiet, Scanner scanner) {
        this.playerNames = playerNames;
        this.isHumanPlayer = isHumanPlayer;
        this.quiet = quiet;
        this.scanner = scanner;
    }

    public boolean runGame() {
        // --- STEP 1: INITIALIZE THE GAME ---
        deck = new UnoDeck();
        deck.shuffle();
        turnManager = new TurnManager(playerNames.size());
        callManager = new UnoCallManager();
        playerHands = new ArrayList<>();

        for (int i = 0; i < playerNames.size(); i++) {
            List<UnoCard> hand = new ArrayList<>();
            for (int c = 0; c < 7; c++) {
                hand.add(deck.getCards().remove(0));
            }
            playerHands.add(hand);
        }

        topCard = deck.getCards().remove(0);
        activeColor = topCard.getColor();
        if (activeColor == CardColor.WILD) {
            activeColor = CardColor.RED;
        }

        logger.info("New game prepared and started. First player: " + playerNames.get(0));

        int guard = 0;

        // --- STEP 2: THE GAME LOOP ---
        while (guard < 3000) {
            guard++;
            int currentPlayerIndex = turnManager.getCurrentPlayerIndex();
            String name = playerNames.get(currentPlayerIndex);
            List<UnoCard> hand = playerHands.get(currentPlayerIndex);
            boolean isHuman = isHumanPlayer.get(currentPlayerIndex);

            logger.info("Turn started for player: " + name);

            if (!quiet) {
                System.out.println("\n------------------------------------------------");
                System.out.println("Up card: " + topCard + (topCard.getColor() == CardColor.WILD ? " (Called Color: " + activeColor + ")" : ""));
                System.out.println(name + "'s hand: " + join(hand));
            }

            // --- STEP 3: PROCESS TURNS ---
            UnoCard chosenCard = null;

            if (isHuman) {
                chosenCard = askHumanPlay(hand);
            } else {
                for (UnoCard card : hand) {
                    if (UnoRuleValidator.isLegalPlay(card, topCard, activeColor)) {
                        chosenCard = card;
                        break;
                    }
                }
            }

            if (chosenCard == null) {
                UnoCard drawn = CardEffectHandler.drawSingleCard(deck, hand);
                logger.info(name + " drew a card.");
                if (!quiet) System.out.println(name + " draws a card and passes.");
            } else {
                hand.remove(chosenCard);
                topCard = chosenCard;
                activeColor = chosenCard.getColor();

                logger.info(name + " successfully played: " + chosenCard);
                if (!quiet) System.out.println(name + " plays " + chosenCard);

                if (chosenCard.getColor() == CardColor.WILD) {
                    if (isHuman) {
                        activeColor = askColor();
                    } else {
                        activeColor = CardColor.RED;
                    }
                    logger.info(name + " calls color: " + activeColor);
                    if (!quiet) System.out.println(name + " calls " + activeColor);
                }

                if (hand.size() == 1) {
                    callManager.callUno();
                    if (!quiet) System.out.println(name + " yells UNO!");
                }

                // --- STEP 5: CALCULATE THE WINNER ---
                if (hand.isEmpty()) {
                    int totalPoints = 0;
                    for (int i = 0; i < playerHands.size(); i++) {
                        if (i != currentPlayerIndex) {
                            totalPoints += UnoScorer.calculateHandScore(playerHands.get(i));
                        }
                    }
                    logger.info("Game finished. Winner: " + name + " with " + totalPoints + " points.");
                    if (!quiet) {
                        System.out.println("\n*** " + name + " wins the round! ***");
                        System.out.println(name + " scores " + totalPoints + " points!");
                    }
                    return true;
                }

                // --- STEP 4: APPLY EFFECTS ---
                int nextIndex = (turnManager.isClockwise() ? (currentPlayerIndex + 1) : (currentPlayerIndex - 1 + playerNames.size())) % playerNames.size();
                List<UnoCard> nextHand = playerHands.get(nextIndex);

                if (chosenCard.getType() == CardType.SKIP) {
                    if (!quiet) System.out.println("Next player is skipped!");
                    turnManager.playSkip();
                } else if (chosenCard.getType() == CardType.REVERSE) {
                    if (!quiet) System.out.println("Direction is reversed!");
                    turnManager.playReverse();
                } else if (chosenCard.getType() == CardType.DRAW_TWO) {
                    if (!quiet) System.out.println("Next player draws two and is skipped!");
                    CardEffectHandler.applyDrawTwo(turnManager, deck, nextHand);
                } else if (chosenCard.getType() == CardType.WILD_DRAW_FOUR) {
                    if (!quiet) System.out.println("Next player draws four and is skipped!");
                    CardEffectHandler.applyWildDrawFour(turnManager, deck, nextHand);
                }
            }

            turnManager.moveToNextPlayer();
        }

        logger.warning("Game stopped at 3000 turn safety limit.");
        if (!quiet) System.out.println("Game stopped at safety limit.");
        return false;
    }

    private UnoCard askHumanPlay(List<UnoCard> hand) {
        while (true) {
            System.out.print("Choose card index (0 to " + (hand.size() - 1) + ") or 'D' to draw: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("D") || input.equals("DRAW")) {
                return null;
            }

            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) {
                    UnoCard card = hand.get(index);
                    if (UnoRuleValidator.isLegalPlay(card, topCard, activeColor)) {
                        return card;
                    } else {
                        System.out.println("That card is not legal to play on top of " + topCard + ".");
                    }
                } else {
                    System.out.println("Invalid index.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number or 'D'.");
            }
        }
    }

    private CardColor askColor() {
        while (true) {
            System.out.print("Call color (R/Y/G/B): ");
            String input = scanner.nextLine().trim().toUpperCase();
            switch (input) {
                case "R": return CardColor.RED;
                case "Y": return CardColor.YELLOW;
                case "G": return CardColor.GREEN;
                case "B": return CardColor.BLUE;
            }
            System.out.println("Invalid color. Use R, Y, G, or B.");
        }
    }

    private String join(List<UnoCard> cards) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            out.append(i).append(":[").append(cards.get(i)).append("] ");
        }
        return out.toString();
    }
}