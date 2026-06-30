import java.util.List;

public class UnoScorer {

    public static int calculateCardValue(UnoCard card) {
        if (card.getType() == CardType.NUMBER) {
            return card.getValue() != null ? card.getValue() : 0;
        }
        if (card.getType() == CardType.SKIP || card.getType() == CardType.REVERSE || card.getType() == CardType.DRAW_TWO) {
            return 20;
        }
        if (card.getType() == CardType.WILD || card.getType() == CardType.WILD_DRAW_FOUR) {
            return 50;
        }
        return 0;
    }

    public static int calculateHandScore(List<UnoCard> hand) {
        return hand.stream().mapToInt(UnoScorer::calculateCardValue).sum();
    }
}