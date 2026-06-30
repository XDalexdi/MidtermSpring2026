import java.util.List;

public class CardEffectHandler {

    // When Draw Two is played, the next player draws two cards and loses their turn.
    public static void applyDrawTwo(TurnManager turnManager, UnoDeck deck, List<UnoCard> nextPlayerHand) {
        drawCards(deck, nextPlayerHand, 2);
        turnManager.playSkip();
    }

    // When Wild Draw Four is played, the next player draws four cards and loses their turn.
    public static void applyWildDrawFour(TurnManager turnManager, UnoDeck deck, List<UnoCard> nextPlayerHand) {
        drawCards(deck, nextPlayerHand, 4);
        turnManager.playSkip();
    }

    // Standard draw behavior when a player cannot play a card.
    public static UnoCard drawSingleCard(UnoDeck deck, List<UnoCard> playerHand) {
        if (deck.getCards().isEmpty()) {
            return null; // In a full game, the discard pile would be reshuffled here
        }
        UnoCard drawnCard = deck.getCards().remove(0);
        playerHand.add(drawnCard);
        return drawnCard;
    }

    // Helper method to draw a specific number of cards
    private static void drawCards(UnoDeck deck, List<UnoCard> hand, int count) {
        for (int i = 0; i < count; i++) {
            if (!deck.getCards().isEmpty()) {
                hand.add(deck.getCards().remove(0));
            }
        }
    }
}