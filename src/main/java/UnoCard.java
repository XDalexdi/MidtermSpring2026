public class UnoCard {
    private final CardColor color;
    private final CardType type;
    private final Integer value; // null for action/wild cards, 0-9 for numbers

    public UnoCard(CardColor color, CardType type, Integer value) {
        this.color = color;
        this.type = type;
        this.value = value;
    }

    public CardColor getColor() { return color; }
    public CardType getType() { return type; }
    public Integer getValue() { return value; }

    @Override
    public String toString() {
        if (type == CardType.NUMBER) {
            return color + " " + value;
        }
        return color + " " + type;
    }
}