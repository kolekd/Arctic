package com.company.model.wall;

import static com.company.logic.Constants.BOARD_WIDTH;
import static com.company.logic.Constants.TILE_SIZE;

public class MovingWall extends Wall {

    private boolean moving;
    private boolean movingRight;

    public MovingWall(int posX, int posY, boolean movingRight) {
        super(posX, posY);
        this.isPlaced = true;
        this.moving = true;
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
