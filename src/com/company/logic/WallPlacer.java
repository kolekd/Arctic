package com.company.logic;

public class WallPlacer {

    boolean placed;
    int posX;

    public WallPlacer(boolean willBePlaced, int posX) {
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
