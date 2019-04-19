package com.company.model.newmodel;

public class Wall extends Tile {

    String justDestroyedBy;
    int scoreDisplayCounter;

    public Wall(int posX, int posY, boolean isPlaced) {
        super(posX, posY, isPlaced);
        this.justDestroyedBy = "";
        this.scoreDisplayCounter = 0;
    }

    public Wall() {}

    public void setJustDestroyedBy(String justDestroyedBy) {
        this.justDestroyedBy = justDestroyedBy;
    }

    public String getJustDestroyedBy() {
        return justDestroyedBy;
    }

    public int getScoreDisplayCounter() {
        return scoreDisplayCounter;
    }

    public void setScoreDisplayCounter(int scoreDisplayCounter) {
        this.scoreDisplayCounter = scoreDisplayCounter;
    }
}
