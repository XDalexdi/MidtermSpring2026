import java.util.Scanner;
import java.util.logging.Logger;
import java.time.Instant;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        logger.info("UNO Application Started.");

        // --- Assignment 5: Query/Report Features ---
        try (UnoGameRepository repo = new UnoGameRepository()) {
            for (String arg : args) {
                if (arg.equals("--history")) {
                    System.out.println("--- Recent Games ---");
                    repo.getRecentGames(5).forEach(g ->
                            System.out.println("Game ID: " + g.getId() + " | Winner: " + g.getWinner()));
                    return;
                } else if (arg.equals("--highscores")) {
                    System.out.println("--- Highest Scores ---");
                    repo.getHighestScores(5).forEach(s ->
                            System.out.println(s.getPlayerName() + ": " + s.getScore()));
                    return;
                } else if (arg.startsWith("--wins=")) {
                    String name = arg.split("=")[1];
                    System.out.println(name + " Total Wins: " + repo.getPlayerWinCount(name));
                    return;
                }
            }
        }

        // --- Assignment 4: Original Setup Logic ---
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
                System.out.println("Usage: java -jar uno-cli-1.0-SNAPSHOT.jar [--bots N] [--games N] [--human] [--quiet] [--seed N] [--history] [--highscores] [--wins=Name]");
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

        // --- Assignment 5: Persistence Integration ---
        try (UnoGameRepository repo = new UnoGameRepository()) {
            GameEngine engine = new GameEngine(state, quiet, scanner);

            for (int g = 1; g <= games; g++) {
                if (!quiet) System.out.println("\n=== Game " + g + " ===");
                logger.info("Starting Game " + g);

                boolean won = engine.runGame();

                if (won) {
                    // Create and Persist the Game Entity
                    GameRecordEntity record = new GameRecordEntity(
                            state.playerNames.get(state.currentPlayer),
                            3000,
                            Instant.now().toString()
                    );

                    // Add scores for all players
                    for (int i = 0; i < state.playerNames.size(); i++) {
                        record.addPlayerScore(new PlayerScoreEntity(state.playerNames.get(i), state.scores[i]));
                    }

                    repo.saveGameRecord(record);
                    logger.info("Game result saved to database.");
                }
            }
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < state.playerNames.size(); i++) {
            System.out.println(state.playerNames.get(i) + ": " + state.scores[i]);
        }

        logger.info("UNO Application Finished.");
    }
}