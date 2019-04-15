package com.company.model;

import java.util.List;

public class WallLine {

    private List<Wall> walls;
    private int posY;

    public WallLine(List<Wall> walls, int posY) {
        this.walls = walls;
        this.posY = posY;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

}
