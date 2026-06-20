import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class UnoTest {

    @Test
    void testCardProperties() {
        assertEquals("R", UnoRules.color("R5"));
        assertEquals("DRAW_TWO", UnoRules.rank("G+2"));
        assertEquals(9, UnoRules.number("B9"));
        assertEquals(50, UnoRules.points("W4"));
        assertEquals(20, UnoRules.points("YS"));
        assertEquals(7, UnoRules.points("R7"));
    }

    @Test
    void testLegalPlays() {
        assertTrue(UnoRules.isLegal("R2", "R9", ""));
        assertTrue(UnoRules.isLegal("G9", "R9", ""));
        assertTrue(UnoRules.isLegal("GS", "YS", ""));
        assertTrue(UnoRules.isLegal("W", "R9", ""));
        assertFalse(UnoRules.isLegal("B3", "R9", ""));
        assertTrue(UnoRules.isLegal("B3", "W", "B"));
    }

    @Test
    void testBotStrategy() {
        ArrayList<String> hand = new ArrayList<>();
        hand.add("B3");
        hand.add("R4");
        hand.add("W");
        // Bot should prioritize playing a normal legal card over a Wild
        assertEquals(1, BotStrategy.chooseCard(hand, "R9", ""));

        ArrayList<String> colors = new ArrayList<>();
        colors.add("B1");
        colors.add("B2");
        colors.add("R3");
        // Bot should choose the color it has the most of (Blue)
        assertEquals("B", BotStrategy.chooseColor(colors));
    }
}