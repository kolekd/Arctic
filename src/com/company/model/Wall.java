package com.company.model;

public class Wall {

    private boolean placed;
    private int posX;

    public Wall(boolean willBePlaced, int posX) {
        this.placed = willBePlaced;
        this.posX = posX;
    }

    public boolean isPlaced() {
        return placed;
    }

    public int getPosX() {
        return posX;
    }
}
