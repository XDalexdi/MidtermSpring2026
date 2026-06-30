import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UnoCallManagerTest {

    @Test
    public void testMissedUnoPenalty() {
        UnoCallManager callManager = new UnoCallManager();
        UnoDeck deck = new UnoDeck();
        List<UnoCard> hand = new ArrayList<>();
        hand.add(new UnoCard(CardColor.RED, CardType.NUMBER, 1));

        assertTrue(callManager.hasMissedUno(hand), "Should detect missed UNO on one-card state");
        callManager.penalizeMissedUno(deck, hand);
        assertEquals(3, hand.size(), "Missed UNO call is penalized by drawing two cards");
    }

    @Test
    public void testValidUnoCall() {
        UnoCallManager callManager = new UnoCallManager();
        List<UnoCard> hand = new ArrayList<>();
        hand.add(new UnoCard(CardColor.RED, CardType.NUMBER, 1));

        callManager.callUno();
        assertFalse(callManager.hasMissedUno(hand), "Valid UNO call avoids penalty");
    }
}