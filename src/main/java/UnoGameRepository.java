import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;

public class UnoGameRepository implements AutoCloseable {
    private final EntityManagerFactory emf;

    public UnoGameRepository() {
        // This MUST match the exact unit name defined inside META-INF/persistence.xml
        this.emf = Persistence.createEntityManagerFactory("uno-unit");
    }

    // Rubric Item 2: Persist Core Game Data safely inside an all-or-nothing transaction
    public void saveGameRecord(GameRecordEntity gameRecord) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(gameRecord); // Saves the game, winner, timestamp, and all player scores
            tx.commit();
        } catch (RuntimeException error) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw error;
        } finally {
            em.close();
        }
    }

    // Rubric Item 3 (Query 1): List recent games
    public List<GameRecordEntity> getRecentGames(int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("""
                select g from GameRecordEntity g
                order by g.completedTimestamp desc
                """, GameRecordEntity.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Rubric Item 3 (Query 2): Show player win count
    public long getPlayerWinCount(String playerName) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery("""
                select count(g) from GameRecordEntity g
                where g.winner = :playerName
                """, Long.class)
                    .setParameter("playerName", playerName)
                    .getSingleResult();
            return count != null ? count : 0;
        } finally {
            em.close();
        }
    }

    // Rubric Item 3 (Query 3): Show highest scores
    public List<PlayerScoreEntity> getHighestScores(int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("""
                select s from PlayerScoreEntity s
                order by s.score desc
                """, PlayerScoreEntity.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}