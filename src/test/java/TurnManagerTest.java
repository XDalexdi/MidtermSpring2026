import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TurnManagerTest {

    @Test
    public void testNextPlayerClockwise() {
        TurnManager tm = new TurnManager(4);
        tm.moveToNextPlayer();
        assertEquals(1, tm.getCurrentPlayerIndex(), "Next player should be index 1");
    }

    @Test
    public void testSkipBehavior() {
        TurnManager tm = new TurnManager(4);
        tm.playSkip();
        assertEquals(2, tm.getCurrentPlayerIndex(), "Player 1 should be skipped, landing on index 2");
    }

    @Test
    public void testReverseBehaviorThreeOrMorePlayers() {
        TurnManager tm = new TurnManager(3);
        tm.playReverse();
        assertEquals(2, tm.getCurrentPlayerIndex(), "Direction should change, moving to previous player");
        assertFalse(tm.isClockwise(), "Direction should be counter-clockwise");
    }

    @Test
    public void testReverseBehaviorTwoPlayers() {
        TurnManager tm = new TurnManager(2);
        tm.playReverse();
        assertEquals(0, tm.getCurrentPlayerIndex(), "In a two-player game, reverse acts as skip, returning to the original player");
    }
}