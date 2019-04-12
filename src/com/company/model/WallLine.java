package com.company.model;

import java.util.List;

public class WallLine {

    List<Wall> walls;
    int posY;

    public WallLine(List<Wall> walls, int posY) {
        this.walls = walls;
        this.posY = posY;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public void setWalls(List<Wall> walls) {
        this.walls = walls;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

}
