package com.company.model;

import java.util.List;

import static com.company.logic.Constants.TILE_SIZE;

public class MovingWallLine extends WallLine {

    public MovingWallLine(List<Wall> walls) {
        super(walls);
    }

    public Wall retrieveMovingWall() {
        for(Wall wall : walls) {
            if (wall instanceof MovingWall) {
                return wall;
            }
        }
        return null;
    }

}
