import java.util.List;

public class UnoCallManager {
    private boolean unoCalled = false;

    public void callUno() {
        this.unoCalled = true;
    }

    public void resetUnoCall() {
        this.unoCalled = false;
    }

    public boolean hasMissedUno(List<UnoCard> hand) {
        // Detects the one-card state and checks if UNO was called
        return hand.size() == 1 && !unoCalled;
    }

    public void penalizeMissedUno(UnoDeck deck, List<UnoCard> hand) {
        if (hasMissedUno(hand)) {
            // The standard penalty is drawing two cards
            for(int i = 0; i < 2; i++) {
                if (!deck.getCards().isEmpty()) {
                    hand.add(deck.getCards().remove(0));
                }
            }
        }
    }
}
