package com.company.model;

public class Wall {

    boolean isPlaced;
    int posX;
    boolean isPowerUp;

    public Wall(boolean placed, int posX) {
        this.isPlaced = placed;
        this.posX = posX;
        this.isPowerUp = false;
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
}
