import java.util.ArrayList;

public class BotStrategy {
    public static int chooseCard(ArrayList<String> hand, String upCard, String calledColor) {
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (UnoRules.isLegal(card, upCard, calledColor) && UnoRules.rank(card).equals("DRAW_TWO")) return i;
        }
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (UnoRules.isLegal(card, upCard, calledColor) && UnoRules.rank(card).equals("SKIP")) return i;
        }
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (UnoRules.isLegal(card, upCard, calledColor) && UnoRules.rank(card).equals("REVERSE")) return i;
        }
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (UnoRules.isLegal(card, upCard, calledColor) && UnoRules.rank(card).equals("NUMBER")) return i;
        }
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).startsWith("W")) return i;
        }
        return -1;
    }

    public static String chooseColor(ArrayList<String> hand) {
        int r = 0, y = 0, g = 0, b = 0;
        for (String c : hand) {
            String color = UnoRules.color(c);
            if (color.equals("R")) r++;
            else if (color.equals("Y")) y++;
            else if (color.equals("G")) g++;
            else if (color.equals("B")) b++;
        }
        if (r >= y && r >= g && r >= b) return "R";
        else if (y >= r && y >= g && y >= b) return "Y";
        else if (g >= r && g >= y && g >= b) return "G";
        else return "B";
    }
}