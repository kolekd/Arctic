package com.company.model;

public class Wall {

    boolean placed;
    int posX;

    public Wall(boolean willBePlaced, int posX) {
        this.placed = willBePlaced;
        this.posX = posX;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }
}
