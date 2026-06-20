import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
public class GameRecordEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String winner;

    @Column(nullable = false)
    private int roundsPlayed;

    @Column(nullable = false)
    private String completedTimestamp;

    // Establishes the primary-foreign key relationship required by the rubric
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PlayerScoreEntity> playerScores = new ArrayList<>();

    protected GameRecordEntity() {
        // JPA needs a no-argument constructor.
    }

    public GameRecordEntity(String winner, int roundsPlayed, String completedTimestamp) {
        this.id = UUID.randomUUID().toString();
        this.winner = winner;
        this.roundsPlayed = roundsPlayed;
        this.completedTimestamp = completedTimestamp;
    }

    public void addPlayerScore(PlayerScoreEntity scoreEntity) {
        playerScores.add(scoreEntity);
        scoreEntity.setGame(this);
    }

    public String getId() { return id; }
    public String getWinner() { return winner; }
    public int getRoundsPlayed() { return roundsPlayed; }
    public String getCompletedTimestamp() { return completedTimestamp; }
    public List<PlayerScoreEntity> getPlayerScores() { return playerScores; }
}