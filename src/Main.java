import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        long seed = System.currentTimeMillis();
        boolean quiet = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) bots = Integer.parseInt(args[++i]);
            else if (args[i].equals("--games") && i + 1 < args.length) games = Integer.parseInt(args[++i]);
            else if (args[i].equals("--human")) human = true;
            else if (args[i].equals("--quiet")) quiet = true;
            else if (args[i].equals("--seed") && i + 1 < args.length) seed = Long.parseLong(args[++i]);
            else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N]");
                return;
            }
        }

        Scanner scanner = new Scanner(System.in);
        GameState state = new GameState(seed);
        state.setupPlayers(bots, human);
        
        if (state.playerNames.size() < 2 || state.playerNames.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        GameEngine engine = new GameEngine(state, quiet, scanner);

        for (int g = 1; g <= games; g++) {
            if (!quiet) System.out.println("\n=== Game " + g + " ===");
            engine.runGame();
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < state.playerNames.size(); i++) {
            System.out.println(state.playerNames.get(i) + ": " + state.scores[i]);
        }
    }

    static void selfTest() {
        int passed = 0;
        int total = 0;

        System.out.println("Running characterization tests...");

        total++; if (UnoRules.color("R5").equals("R")) passed++; else fail("color R5");
        total++; if (UnoRules.rank("G+2").equals("DRAW_TWO")) passed++; else fail("rank +2");
        total++; if (UnoRules.number("B9") == 9) passed++; else fail("number B9");
        total++; if (UnoRules.points("W4") == 50) passed++; else fail("wild points");
        total++; if (UnoRules.points("YS") == 20) passed++; else fail("skip points");
        total++; if (UnoRules.points("R7") == 7) passed++; else fail("number points");

        total++; if (UnoRules.isLegal("R2", "R9", "")) passed++; else fail("match by color");
        total++; if (UnoRules.isLegal("G9", "R9", "")) passed++; else fail("match by number");
        total++; if (UnoRules.isLegal("GS", "YS", "")) passed++; else fail("match by action (skip)");
        total++; if (UnoRules.isLegal("B+2", "R+2", "")) passed++; else fail("match by action (+2)");
        total++; if (!UnoRules.isLegal("B3", "R9", "")) passed++; else fail("illegal mismatch");

        total++; if (UnoRules.isLegal("W", "R9", "")) passed++; else fail("wild is always legal");
        total++; if (UnoRules.isLegal("W4", "B2", "")) passed++; else fail("wild4 is always legal");
        total++; if (UnoRules.isLegal("B3", "W", "B")) passed++; else fail("match called color");
        total++; if (!UnoRules.isLegal("G4", "W", "B")) passed++; else fail("mismatch called color");

        ArrayList<String> h1 = new ArrayList<>();
        h1.add("B3"); h1.add("R4"); h1.add("W");
        total++; if (BotStrategy.chooseCard(h1, "R9", "") == 1) passed++; else fail("bot plays normal before wild");

        ArrayList<String> h2 = new ArrayList<>();
        h2.add("B1"); h2.add("B2"); h2.add("R3");
        total++; if (BotStrategy.chooseColor(h2).equals("B")) passed++; else fail("bot color choice based on max count");

        ArrayList<String> h3 = new ArrayList<>();
        h3.add("W"); h3.add("R5"); h3.add("RS"); h3.add("R+2");
        total++; if (BotStrategy.chooseCard(h3, "R9", "") == 3) passed++; else fail("bot prioritizes draw two");

        ArrayList<String> h4 = new ArrayList<>();
        h4.add("W"); h4.add("R5"); h4.add("RS");
        total++; if (BotStrategy.chooseCard(h4, "R9", "") == 2) passed++; else fail("bot prioritizes skip");

        GameState testEmpty = new GameState(1L);
        testEmpty.deck.clear();
        testEmpty.discard.clear();
        total++; if (testEmpty.drawCard().equals("W")) passed++; else fail("empty deck and discard yields W");

        // The professor's requested deterministic runtime check:
        total++; 
        GameState deterministicState = new GameState(42L);
        deterministicState.setupPlayers(2, false); 
        GameEngine testEngine = new GameEngine(deterministicState, true, new Scanner(System.in));
        boolean finished = testEngine.runGame();
        if (finished) passed++; else fail("deterministic runtime game finishes without hitting 3000 safety limit");

        System.out.println("Passed " + passed + " / " + total + " characterization checks.");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }
}
