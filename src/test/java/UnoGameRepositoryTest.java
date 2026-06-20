import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UnoGameRepositoryTest {
    private UnoGameRepository repository;

    @BeforeEach
    void setUp() {
        // Initialize an isolated repository instance before each test
        repository = new UnoGameRepository();
    }

    @AfterEach
    void tearDown() {
        // Ensure the persistence context is closed
        if (repository != null) {
            repository.close();
        }
    }

    @Test
    void testPersistAndQueryGameData() {
        // 1. Create a simulated game record with players, winner, rounds, and timestamp
        GameRecordEntity game = new GameRecordEntity("Alex", 14, Instant.now().toString());
        game.addPlayerScore(new PlayerScoreEntity("Alex", 500));
        game.addPlayerScore(new PlayerScoreEntity("Bot_1", 120));
        game.addPlayerScore(new PlayerScoreEntity("Bot_2", 45));

        // Save the game record to our embedded H2 database
        repository.saveGameRecord(game);

        // 2. Verify Rubric Item 3 (Query 1): Recent games list
        List<GameRecordEntity> recent = repository.getRecentGames(5);
        assertFalse(recent.isEmpty(), "Recent games list should not be empty");
        assertEquals("Alex", recent.get(0).getWinner(), "Winner should be persisted correctly");
        assertEquals(3, recent.get(0).getPlayerScores().size(), "All 3 player scores should be cascaded and saved");

        // 3. Verify Rubric Item 3 (Query 2): Player win count
        long winCount = repository.getPlayerWinCount("Alex");
        assertEquals(1, winCount, "Alex should have exactly 1 win recorded");

        // 4. Verify Rubric Item 3 (Query 3): Highest scores list
        List<PlayerScoreEntity> highScores = repository.getHighestScores(1);
        assertFalse(highScores.isEmpty(), "Highest scores list should not be empty");
        assertEquals(500, highScores.get(0).getScore(), "Highest score should be 500");
        assertEquals("Alex", highScores.get(0).getPlayerName(), "Top scorer should be Alex");
    }
}