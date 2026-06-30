import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CardEffectHandlerTest {

    @Test
    public void testDrawTwoBehavior() {
        TurnManager tm = new TurnManager(3);
        UnoDeck deck = new UnoDeck(); // Starts with 108 cards
        List<UnoCard> nextPlayerHand = new ArrayList<>();

        CardEffectHandler.applyDrawTwo(tm, deck, nextPlayerHand);

        assertEquals(2, nextPlayerHand.size(), "Next player should draw exactly two cards");
        assertEquals(106, deck.size(), "Deck should have 2 fewer cards");
        assertEquals(2, tm.getCurrentPlayerIndex(), "Next player should be skipped");
    }

    @Test
    public void testWildDrawFourBehavior() {
        TurnManager tm = new TurnManager(3);
        UnoDeck deck = new UnoDeck();
        List<UnoCard> nextPlayerHand = new ArrayList<>();

        CardEffectHandler.applyWildDrawFour(tm, deck, nextPlayerHand);

        assertEquals(4, nextPlayerHand.size(), "Next player should draw exactly four cards");
        assertEquals(104, deck.size(), "Deck should have 4 fewer cards");
        assertEquals(2, tm.getCurrentPlayerIndex(), "Next player should be skipped");
    }

    @Test
    public void testDrawSingleCard() {
        UnoDeck deck = new UnoDeck();
        List<UnoCard> playerHand = new ArrayList<>();

        UnoCard drawn = CardEffectHandler.drawSingleCard(deck, playerHand);

        assertNotNull(drawn, "A card should be drawn");
        assertEquals(1, playerHand.size(), "Player hand should contain 1 card");
        assertEquals(107, deck.size(), "Deck should have 1 fewer card");
    }
}