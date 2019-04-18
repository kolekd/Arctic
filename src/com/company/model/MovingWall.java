package com.company.model;

import com.company.util.RandomDecision;

import static com.company.logic.Constants.BOARD_WIDTH;
import static com.company.logic.Constants.TILE_SIZE;

public class MovingWall extends Wall {

    boolean movingRight;

    public MovingWall(boolean placed, int posX) {
        super(placed, posX);
        this.movingRight = RandomDecision.get();
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public void bounceIfAtBorder() {
        if(posX <= 0) {
            movingRight = true;
        } else if (posX >= BOARD_WIDTH - TILE_SIZE) {
            movingRight = false;
        }
    }
}
