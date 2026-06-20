import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "scores")
public class PlayerScoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String playerName;

    @Column(nullable = false)
    private int score;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private GameRecordEntity game;

    protected PlayerScoreEntity() {
        // JPA needs a no-argument constructor.
    }

    public PlayerScoreEntity(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public void setGame(GameRecordEntity game) { this.game = game; }
}