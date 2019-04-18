package com.company.model;

public class Wall {

    boolean isPlaced;
    int posX;
    boolean isPowerUp;
    String justDestroyed;
    int scoreDisplayCounter;

    public Wall(boolean placed, int posX) {
        this.isPlaced = placed;
        this.posX = posX;
        this.isPowerUp = false;
        this.justDestroyed = "";
        this.scoreDisplayCounter = 0;
    }

    public Wall() {}

    public boolean isPlaced() {
        return isPlaced;
    }

    public void setPlaced(boolean placed) {
        this.isPlaced = placed;
    }

    public boolean isPowerUp() {
        return isPowerUp;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setJustDestroyed(String justDestroyed) {
        this.justDestroyed = justDestroyed;
    }

    public String getJustDestroyed() {
        return justDestroyed;
    }

    public int getScoreDisplayCounter() {
        return scoreDisplayCounter;
    }

    public void setScoreDisplayCounter(int scoreDisplayCounter) {
        this.scoreDisplayCounter = scoreDisplayCounter;
    }
}
