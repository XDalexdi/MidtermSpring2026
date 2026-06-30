import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnoDeck {
    private final List<UnoCard> cards = new ArrayList<>();

    public UnoDeck() {
        initializeDeck();
    }

    private void initializeDeck() {
        CardColor[] playableColors = {CardColor.RED, CardColor.YELLOW, CardColor.GREEN, CardColor.BLUE};

        for (CardColor color : playableColors) {
            // One 0 card per color
            cards.add(new UnoCard(color, CardType.NUMBER, 0));

            // Two of each 1-9 per color
            for (int i = 1; i <= 9; i++) {
                cards.add(new UnoCard(color, CardType.NUMBER, i));
                cards.add(new UnoCard(color, CardType.NUMBER, i));
            }

            // Action cards: 2x Skip, 2x Reverse, 2x Draw Two per color
            for (int i = 0; i < 2; i++) {
                cards.add(new UnoCard(color, CardType.SKIP, null));
                cards.add(new UnoCard(color, CardType.REVERSE, null));
                cards.add(new UnoCard(color, CardType.DRAW_TWO, null));
            }
        }

        // Wilds: 4x Wild, 4x Wild Draw Four
        for (int i = 0; i < 4; i++) {
            cards.add(new UnoCard(CardColor.WILD, CardType.WILD, null));
            cards.add(new UnoCard(CardColor.WILD, CardType.WILD_DRAW_FOUR, null));
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<UnoCard> getCards() {
        return cards;
    }

    public int size() {
        return cards.size();
    }
}