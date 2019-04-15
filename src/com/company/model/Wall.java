package com.company.model;

public class Wall {

    private boolean placed;
    private int posX;
    private boolean isPowerUp;

    public Wall(boolean placed, int posX, boolean isPower) {
        this.placed = placed;
        this.posX = posX;
        this.isPowerUp = isPower;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    public boolean isPowerUp() {
        return isPowerUp;
    }

    public int getPosX() {
        return posX;
    }
}
