import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UnoScorerTest {

    @Test
    public void testCardValues() {
        assertEquals(5, UnoScorer.calculateCardValue(new UnoCard(CardColor.RED, CardType.NUMBER, 5)), "Number cards are face value");
        assertEquals(20, UnoScorer.calculateCardValue(new UnoCard(CardColor.BLUE, CardType.SKIP, null)), "Action cards are 20 points");
        assertEquals(50, UnoScorer.calculateCardValue(new UnoCard(CardColor.WILD, CardType.WILD_DRAW_FOUR, null)), "Wild cards are 50 points");
    }

    @Test
    public void testHandScore() {
        List<UnoCard> hand = new ArrayList<>();
        hand.add(new UnoCard(CardColor.RED, CardType.NUMBER, 5));
        hand.add(new UnoCard(CardColor.BLUE, CardType.SKIP, null));

        assertEquals(25, UnoScorer.calculateHandScore(hand), "Remaining cards are scored by summing their values");
    }
}