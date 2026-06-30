import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnoRuleValidatorTest {

    @Test
    public void testMatchByColor() {
        UnoCard topCard = new UnoCard(CardColor.RED, CardType.NUMBER, 5);
        UnoCard cardToPlay = new UnoCard(CardColor.RED, CardType.NUMBER, 9);
        assertTrue(UnoRuleValidator.isLegalPlay(cardToPlay, topCard, CardColor.RED), "Should match by color");
    }

    @Test
    public void testMatchByNumber() {
        UnoCard topCard = new UnoCard(CardColor.BLUE, CardType.NUMBER, 5);
        UnoCard cardToPlay = new UnoCard(CardColor.RED, CardType.NUMBER, 5);
        assertTrue(UnoRuleValidator.isLegalPlay(cardToPlay, topCard, CardColor.BLUE), "Should match by number");
    }

    @Test
    public void testMatchByActionType() {
        UnoCard topCard = new UnoCard(CardColor.GREEN, CardType.SKIP, null);
        UnoCard cardToPlay = new UnoCard(CardColor.YELLOW, CardType.SKIP, null);
        assertTrue(UnoRuleValidator.isLegalPlay(cardToPlay, topCard, CardColor.GREEN), "Should match by action type");
    }

    @Test
    public void testWildCardsAlwaysPlayable() {
        UnoCard topCard = new UnoCard(CardColor.BLUE, CardType.NUMBER, 2);
        UnoCard wildCard = new UnoCard(CardColor.WILD, CardType.WILD, null);
        UnoCard wildDrawFour = new UnoCard(CardColor.WILD, CardType.WILD_DRAW_FOUR, null);
        assertTrue(UnoRuleValidator.isLegalPlay(wildCard, topCard, CardColor.BLUE), "Wild cards are always playable");
        assertTrue(UnoRuleValidator.isLegalPlay(wildDrawFour, topCard, CardColor.BLUE), "Wild Draw Four cards are always playable");
    }

    @Test
    public void testIllegalPlayRejected() {
        UnoCard topCard = new UnoCard(CardColor.BLUE, CardType.NUMBER, 2);
        UnoCard cardToPlay = new UnoCard(CardColor.RED, CardType.NUMBER, 3);
        assertFalse(UnoRuleValidator.isLegalPlay(cardToPlay, topCard, CardColor.BLUE), "Illegal plays should be rejected");
    }
}