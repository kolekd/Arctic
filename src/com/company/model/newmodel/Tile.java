package com.company.model.newmodel;

public class Tile {

    int posX;
    int posY;
    boolean isPlaced;

    public Tile(int posX, int posY, boolean isPlaced) {
        this.posX = posX;
        this.posY = posY;
        this.isPlaced = isPlaced;
    }

    public Tile() {
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public void setPlaced(boolean placed) {
        isPlaced = placed;
    }
}
