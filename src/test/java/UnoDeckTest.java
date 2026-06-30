import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnoDeckTest {

    @Test
    public void testDeckCompositionSize() {
        UnoDeck deck = new UnoDeck();
        assertEquals(108, deck.size(), "Deck must contain exactly 108 cards.");
    }

    @Test
    public void testWildCardCounts() {
        UnoDeck deck = new UnoDeck();
        long wildCount = deck.getCards().stream()
                .filter(c -> c.getType() == CardType.WILD)
                .count();
        long wildDrawFourCount = deck.getCards().stream()
                .filter(c -> c.getType() == CardType.WILD_DRAW_FOUR)
                .count();

        assertEquals(4, wildCount, "Should have 4 Wild cards.");
        assertEquals(4, wildDrawFourCount, "Should have 4 Wild Draw Four cards.");
    }
}