package cz.danik.arctic.model.scoreboard;

public class Score {

    private int playerNumber;
    private int playerScore;

    public Score(int playerNumber, int playerScore) {
        this.playerNumber = playerNumber;
        this.playerScore = playerScore;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }
}
