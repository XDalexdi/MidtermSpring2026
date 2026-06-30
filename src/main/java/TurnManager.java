public class TurnManager {
    private int currentPlayerIndex;
    private int totalPlayers;
    private boolean clockwise;

    public TurnManager(int totalPlayers) {
        this.totalPlayers = totalPlayers;
        this.currentPlayerIndex = 0;
        this.clockwise = true;
    }

    public void moveToNextPlayer() {
        if (clockwise) {
            currentPlayerIndex = (currentPlayerIndex + 1) % totalPlayers;
        } else {
            currentPlayerIndex = (currentPlayerIndex - 1 + totalPlayers) % totalPlayers;
        }
    }

    // When Skip is played, the next player loses their turn[cite: 3].
    public void playSkip() {
        moveToNextPlayer();
        moveToNextPlayer();
    }

    // When Reverse is played, direction changes[cite: 3].
    // In a two-player game, Reverse is treated like Skip[cite: 3].
    public void playReverse() {
        if (totalPlayers == 2) {
            playSkip();
        } else {
            clockwise = !clockwise;
            moveToNextPlayer();
        }
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public boolean isClockwise() {
        return clockwise;
    }
}