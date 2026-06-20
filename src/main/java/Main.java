import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        logger.info("UNO Application Started.");

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
                System.out.println("Characterization checks have been migrated to JUnit. Please run 'mvn test' instead.");
                return;
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: java -jar target/uno-cli-1.0-SNAPSHOT.jar [--bots N] [--games N] [--human] [--quiet] [--seed N]");
                return;
            }
        }

        Scanner scanner = new Scanner(System.in);
        GameState state = new GameState(seed);
        state.setupPlayers(bots, human);

        if (state.playerNames.size() < 2 || state.playerNames.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            logger.warning("Game initialization failed: Invalid number of players (" + state.playerNames.size() + ")");
            return;
        }

        GameEngine engine = new GameEngine(state, quiet, scanner);

        for (int g = 1; g <= games; g++) {
            if (!quiet) System.out.println("\n=== Game " + g + " ===");
            logger.info("Starting Game " + g);
            engine.runGame();
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < state.playerNames.size(); i++) {
            System.out.println(state.playerNames.get(i) + ": " + state.scores[i]);
        }

        logger.info("UNO Application Finished.");
    }
}