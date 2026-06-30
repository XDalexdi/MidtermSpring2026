public class UnoRuleValidator {

    public static boolean isLegalPlay(UnoCard cardToPlay, UnoCard topDiscardCard, CardColor activeColor) {
        // Wild cards (Wild, Wild Draw Four) are always legal to play
        if (cardToPlay.getColor() == CardColor.WILD) {
            return true;
        }

        // Match by color (using the activeColor, which accounts for previous wild cards)
        if (cardToPlay.getColor() == activeColor) {
            return true;
        }

        // Match by number
        if (cardToPlay.getType() == CardType.NUMBER && topDiscardCard.getType() == CardType.NUMBER) {
            if (cardToPlay.getValue() != null && cardToPlay.getValue().equals(topDiscardCard.getValue())) {
                return true;
            }
        }

        // Match by action type (Skip, Reverse, Draw Two)
        if (cardToPlay.getType() != CardType.NUMBER && cardToPlay.getType() == topDiscardCard.getType()) {
            return true;
        }

        // If none of the above match, the play is illegal
        return false;
    }
}