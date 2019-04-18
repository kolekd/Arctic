package com.company.model;

import com.company.util.RandomDecision;

import static com.company.logic.Constants.BOARD_WIDTH;
import static com.company.logic.Constants.TILE_SIZE;

public class MovingWall extends Wall {

    boolean moving;
    boolean movingRight;

    public MovingWall(boolean placed, int posX) {
        super(placed, posX);
        this.moving = true;
        this.movingRight = RandomDecision.get();
    }

    public void bounceIfAtBorder() {
        if(posX <= 0) {
            movingRight = true;
        } else if (posX >= BOARD_WIDTH - TILE_SIZE) {
            movingRight = false;
        }
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }
}
