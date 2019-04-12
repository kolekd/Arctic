package com.company.logic;

import java.util.List;

public class WallLine {

    List<WallPlacer> walls;
    int posY;

    public WallLine(List<WallPlacer> walls, int posY) {
        this.walls = walls;
        this.posY = posY;
    }

    public List<WallPlacer> getWalls() {
        return walls;
    }

    public void setWalls(List<WallPlacer> walls) {
        this.walls = walls;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
}
