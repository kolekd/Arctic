package com.company.model;

import static com.company.logic.Constants.BOARD_WIDTH;
import static com.company.logic.Constants.TILE_SIZE;

public class MovingWall extends Wall {

    boolean moving;
    boolean movingRight;

    public MovingWall(int posX, int posY, boolean isPlaced, boolean moving, boolean movingRight) {
        super(posX, posY, isPlaced);
        this.moving = moving;
        this.movingRight = movingRight;
    }

    public void bounceIfAtBorder() {
        if(posX <= 0) {
            movingRight = true;
        } else if (posX >= BOARD_WIDTH - TILE_SIZE) {
            movingRight = false;
        }
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }
}
